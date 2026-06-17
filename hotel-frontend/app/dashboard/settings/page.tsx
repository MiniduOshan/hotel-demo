"use client";

import Settings from "../Settings";
import DashboardLayout from "@/components/layout/DashboardLayout";

export default function PageWrapper(props: any) {
  return (
    <DashboardLayout>
      <Settings {...props} />
    </DashboardLayout>
  );
}
