"use client";

import Offers from "../Offers";
import DashboardLayout from "@/components/layout/DashboardLayout";

export default function PageWrapper(props: any) {
  return (
    <DashboardLayout>
      <Offers {...props} />
    </DashboardLayout>
  );
}
