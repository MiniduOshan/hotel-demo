"use client";

import AdminBookings from "../AdminBookings";
import DashboardLayout from "@/components/layout/DashboardLayout";

export default function PageWrapper(props: any) {
  return (
    <DashboardLayout>
      <AdminBookings {...props} />
    </DashboardLayout>
  );
}
