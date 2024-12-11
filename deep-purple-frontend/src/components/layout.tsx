import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import { AppSidebar } from "@/components/app-sidebar";
import routes from "@/routes"; // Import your routes

export default function Layout() {
  return (
    <Router>
      <SidebarProvider>
        <AppSidebar />
        <main style={{ marginLeft: "10px", padding: "20px" }}> {/* Adjust margin for sidebar */}
          <SidebarTrigger />
          <Routes>
            {routes.map((route, index) => (
              <Route
                key={index}
                path={route.path}
                element={<route.component />}
              />
            ))}
          </Routes>
        </main>
      </SidebarProvider>
    </Router>
  );
}
