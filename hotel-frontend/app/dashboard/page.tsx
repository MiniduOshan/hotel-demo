"use client";

import DashboardHome from "./DashboardHome";
import DashboardLayout from "@/components/layout/DashboardLayout";

export default function PageWrapper(props: any) {
  return (
    <DashboardLayout>
      <DashboardHome {...props} />
    </DashboardLayout>
  );
}
