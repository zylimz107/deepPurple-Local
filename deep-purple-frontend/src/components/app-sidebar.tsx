import { FileChartPie, Home, Inbox, Search, Settings, Users, Activity, User } from "lucide-react";
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/ui/sidebar";
import { Link } from "react-router-dom"; // Import Link for navigation

// Menu items with role-based access
const items = [
  {
    title: "Home",
    url: "/home",
    icon: Home,
    roles: ["admin", "user"], // Accessible by both admins and users
  },
  {
    title: "Inbox",
    url: "/inbox",
    icon: Inbox,
    roles: ["admin", "user"],
  },
  {
    title: "Emotion Model Manager",
    url: "/emotion",
    icon: Activity,
    roles: ["user"], // Accessible only by users
  },
  {
    title: "Search",
    url: "/search",
    icon: Search,
    roles: ["admin", "user"],
  },
  {
    title: "Settings",
    url: "/settings",
    icon: Settings,
    roles: ["admin", "user"],
  },
  {
    title: "Analysis",
    url: "/analysis",
    icon: FileChartPie,
    roles: ["user"], // Accessible only by users
  },
  {
    title: "Admin Dashboard",
    url: "/admin",
    icon: Users,
    roles: ["admin"], // Accessible only by admins
  },
  {
    title: "User Dashboard",
    url: "/user",
    icon: User,
    roles: ["user"], // Accessible only by users
  },
];

export function AppSidebar({ userRole }: { userRole: string | null }) {
  // Filter items based on the userRole
  const filteredItems = items.filter((item) => item.roles.includes(userRole || ""));

  return (
    <Sidebar>
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>Application</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {filteredItems.map((item) => (
                <SidebarMenuItem key={item.title}>
                  <SidebarMenuButton asChild>
                    <Link to={item.url}>
                      <item.icon />
                      <span>{item.title}</span>
                    </Link>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
    </Sidebar>
  );
}
