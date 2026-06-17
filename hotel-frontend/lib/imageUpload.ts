/**
 * Helper to upload image files to the Spring Boot backend
 * which in turn uploads them to Supabase Storage and returns CDN URLs.
 */
export async function uploadImage(file: File): Promise<{ url: string; thumbnailUrl: string }> {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch("/api/images/upload", {
    method: "POST",
    body: formData,
  });

  if (!response.ok) {
    const errData = await response.json().catch(() => ({}));
    throw new Error(errData.error || "Image upload failed");
  }

  return response.json();
}

/**
 * Gets the thumbnail version of a Supabase image URL.
 * If the image is not from Supabase or doesn't have an extension, returns the original URL.
 */
export function getThumbnailUrl(imageUrl: string): string {
  if (!imageUrl) return "";
  // Check if it's a Supabase storage URL and doesn't already contain _thumb
  if (imageUrl.includes("supabase.co") && !imageUrl.includes("_thumb.")) {
    const lastDot = imageUrl.lastIndexOf(".");
    if (lastDot > imageUrl.lastIndexOf("/")) {
      return imageUrl.substring(0, lastDot) + "_thumb" + imageUrl.substring(lastDot);
    }
  }
  return imageUrl;
}

