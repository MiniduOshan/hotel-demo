"use client";

import RoomsManagement from "../Rooms";
import DashboardLayout from "@/components/layout/DashboardLayout";

export default function PageWrapper(props: any) {
  return (
    <DashboardLayout>
      <RoomsManagement {...props} />
    </DashboardLayout>
  );
}
