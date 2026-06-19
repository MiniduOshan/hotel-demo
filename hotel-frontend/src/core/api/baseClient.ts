import axios from "axios";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

export const baseClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // Crucial for HttpOnly cookies
});

baseClient.interceptors.request.use(
  (config) => {
    if (typeof window !== "undefined") {
      const email = localStorage.getItem("user-email") || "";
      const tenantId = localStorage.getItem("tenant-id") || "";
      const hotelId = localStorage.getItem("hotel-id") || "";
      const token = localStorage.getItem("token") || "";

      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      if (email) {
        config.headers["x-owner-email"] = email;
        config.headers["x-recipient-email"] = email;
      }
      if (tenantId) {
        config.headers["X-Tenant-ID"] = tenantId;
      }
      if (hotelId) {
        config.headers["x-hotel-id"] = hotelId;
      }
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

let isRefreshing = false;
let failedQueue: any[] = [];

const processQueue = (error: any, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

baseClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // If a 401 occurs and it wasn't already a retry/refresh request
    if (error.response?.status === 401 && !originalRequest._retry && originalRequest.url !== "/api/auth/refresh" && originalRequest.url !== "/api/auth/login") {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return baseClient(originalRequest);
          })
          .catch((err) => {
            return Promise.reject(err);
          });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const res = await axios.post(`${API_BASE_URL}/api/auth/refresh`, {}, { withCredentials: true });
        if (res.data && res.data.accessToken) {
          const newToken = res.data.accessToken;
          localStorage.setItem("token", newToken);
          baseClient.defaults.headers.common["Authorization"] = `Bearer ${newToken}`;
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
          processQueue(null, newToken);
          return baseClient(originalRequest);
        }
      } catch (refreshError) {
        processQueue(refreshError, null);
        // Clear auth state since refresh token is invalid/expired
        localStorage.removeItem("token");
        localStorage.removeItem("user-email");
        localStorage.removeItem("yme_current_user");
        if (typeof window !== "undefined") {
          window.dispatchEvent(new Event("storage"));
        }
      } finally {
        isRefreshing = false;
      }
    }

    console.error("API error response:", error.response || error.message);
    return Promise.reject(error);
  }
);
