"use client";

import MyHotel from "../MyHotel";
import DashboardLayout from "@/components/layout/DashboardLayout";

export default function PageWrapper(props: any) {
  return (
    <DashboardLayout>
      <MyHotel {...props} />
    </DashboardLayout>
  );
}
