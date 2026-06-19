"use client";

import Reviews from "../Reviews";
import DashboardLayout from "@/components/layout/DashboardLayout";

export default function PageWrapper(props: any) {
  return (
    <DashboardLayout>
      <Reviews {...props} />
    </DashboardLayout>
  );
}
