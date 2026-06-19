"use client";

import AdminUsers from "../AdminUsers";
import DashboardLayout from "@/components/layout/DashboardLayout";

export default function PageWrapper(props: any) {
  return (
    <DashboardLayout>
      <AdminUsers {...props} />
    </DashboardLayout>
  );
}
