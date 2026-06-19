"use client";

import Bookings from "../Bookings";
import DashboardLayout from "@/components/layout/DashboardLayout";

export default function PageWrapper(props: any) {
  return (
    <DashboardLayout>
      <Bookings {...props} />
    </DashboardLayout>
  );
}
