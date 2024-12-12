import React from "react";
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import { AppSidebar } from "@/components/app-sidebar";

export default function Layout({ children, userRole }: { children: React.ReactNode; userRole: string | null }) {
  return (
    <SidebarProvider>
      <AppSidebar userRole={userRole} />
      <main>
        <SidebarTrigger />
        {children}
      </main>
    </SidebarProvider>
  );
}
