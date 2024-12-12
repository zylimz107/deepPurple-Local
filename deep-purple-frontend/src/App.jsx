import React, { useState } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import LoginRegister from "@/components/LoginPage/Login";
import Layout from "@/components/layout";
import AdminDashboardPage from "@/pages/AdminDashboardPage";
import UserDashboard from "@/pages/UserDashboard";
import EmotionCategoryPage from "@/pages/EmotionCategoryPage";
import AnalysisPage from "@/pages/AnalysisPage";
import SettingsPage from "@/pages/SettingsPage";
import SearchPage from "@/pages/SearchPage";
import InboxPage from "@/pages/InboxPage";
import { Button } from "@/components/ui/button";
import { LogOut } from "lucide-react";

const App = () => {
  const [userRole, setUserRole] = useState(() => localStorage.getItem("userRole") || null);

  const handleLogin = (role) => {
    setUserRole(role);
    localStorage.setItem("userRole", role);
  };

  const handleLogout = () => {
    setUserRole(null);
    localStorage.removeItem("userRole");
  };

  const ProtectedRoute = ({ element, allowedRoles }) => {
    return allowedRoles.includes(userRole) ? element : <Navigate to="/" />;
  };

  return (
    <Router>
      {!userRole && <LoginRegister onLogin={handleLogin} />}

      {/* Show Logout Button if logged in */}
      {userRole && (
        <Button className="text-center" onClick={handleLogout} style={{ position: "absolute", top: 10, right: 10 }}>
          <LogOut /> Logout
        </Button>
      )}

      <Routes>
        {/* Public Route */}
        <Route path="/" element={<AnalysisPage />} />

        {/* Shared Authenticated Routes */}
        {userRole && (
          <Route
            path="/*"
            element={
              <Layout userRole={userRole}>
                <Routes>
                  <Route path="analysis" element={<AnalysisPage />} />
                  <Route path="inbox" element={<InboxPage />} />
                  <Route path="search" element={<SearchPage />} />
                  <Route path="settings" element={<SettingsPage />} />
                  <Route path="emotion" element={<EmotionCategoryPage />} />
                </Routes>
              </Layout>
            }
          />
        )}

        {/* Admin Routes */}
        <Route
          path="/admin/*"
          element={
            <ProtectedRoute
              allowedRoles={["admin"]}
              element={
                <Layout userRole={userRole}>
                  <Routes>
                    <Route path="/" element={<AdminDashboardPage />} />
                  </Routes>
                </Layout>
              }
            />
          }
        />

        {/* User Routes */}
        <Route
          path="/user/*"
          element={
            <ProtectedRoute
              allowedRoles={["user"]}
              element={
                <Layout userRole={userRole}>
                  <Routes>
                    <Route path="/" element={<UserDashboard />} />
                  </Routes>
                </Layout>
              }
            />
          }
        />
      </Routes>
    </Router>
  );
};

export default App;
