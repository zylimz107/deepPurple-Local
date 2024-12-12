import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { loginUser, registerUser } from "@/api"; // Import API functions
import { useState } from "react";
import { useNavigate } from "react-router-dom"; // Import useNavigate

export default function LoginRegister({ onLogin }) {
  const [loginData, setLoginData] = useState({ username: "", password: "" });
  const [registerData, setRegisterData] = useState({ username: "", password: "" });
  const [loginError, setLoginError] = useState(null); // State for login errors
  const [registerError, setRegisterError] = useState(null); // State for register errors
  const navigate = useNavigate(); // Initialize navigate hook

  // Login handler
  const handleLogin = async () => {
    try {
      setLoginError(null); // Clear previous error
      const response = await loginUser(loginData.username, loginData.password);
      
      console.log("Login response:", response); // Log response to check structure
      
      const { role } = response.data; // Extract role from response
      if (onLogin) onLogin(role); // Optional: Notify parent about login
      
      if (role === "admin") {
        navigate("/admin");
      } else if (role === "user") {
        navigate("/user");
      } else {
        console.error("Unknown role:", role);
      }
    } catch (error) {
      console.error("Login error:", error.message);
      setLoginError("Invalid username or password. Please try again."); // Set login error message
    }
  };

  // Register handler
  const handleRegister = async () => {
    try {
      setRegisterError(null); // Clear previous error
      const response = await registerUser(registerData.username, registerData.password);
      
      console.log("Register response:", response); // Log response to check structure
      
      // Optional: Auto-login or show success message
      navigate("/"); // Redirect to login after successful registration
    } catch (error) {
      console.error("Registration error:", error.message);
      setRegisterError("Registration failed. Please try again."); // Set register error message
    }
  };

  return (
    <div>
      <Tabs defaultValue="account" className="w-[400px] mx-5 my-5">
        <TabsList className="grid w-full grid-cols-2 bg-slate-700 text-background">
          <TabsTrigger value="login">Login</TabsTrigger>
          <TabsTrigger value="register">Register</TabsTrigger>
        </TabsList>

        {/* Login Tab */}
        <TabsContent value="login">
          <Card className="h-[350px]">
            <CardHeader>
              <CardTitle>Login</CardTitle>
              <CardDescription>For all users</CardDescription>
            </CardHeader>
            <CardContent className="space-y-2">
              {loginError && <p className="text-red-500">{loginError}</p>} {/* Display login error */}
              <div className="space-y-1">
                <Label htmlFor="login-username">Username</Label>
                <Input
                  id="login-username"
                  value={loginData.username}
                  onChange={(e) => setLoginData({ ...loginData, username: e.target.value })}
                  required
                />
              </div>
              <div className="space-y-1">
                <Label htmlFor="login-password">Password</Label>
                <Input
                  id="login-password"
                  type="password"
                  value={loginData.password}
                  onChange={(e) => setLoginData({ ...loginData, password: e.target.value })}
                />
              </div>
            </CardContent>
            <CardFooter className="justify-center">
              <Button onClick={handleLogin}>Login</Button>
            </CardFooter>
          </Card>
        </TabsContent>

        {/* Register Tab */}
        <TabsContent value="register">
          <Card className="h-[350px]">
            <CardHeader>
              <CardTitle>Register Account</CardTitle>
              <CardDescription>If you do not have an account, register here</CardDescription>
            </CardHeader>
            <CardContent className="space-y-2">
              {registerError && <p className="text-red-500">{registerError}</p>} {/* Display register error */}
              <div className="space-y-1">
                <Label htmlFor="register-username">Username</Label>
                <Input
                  id="register-username"
                  value={registerData.username}
                  onChange={(e) => setRegisterData({ ...registerData, username: e.target.value })}
                />
              </div>
              <div className="space-y-1">
                <Label htmlFor="register-password">Password</Label>
                <Input
                  id="register-password"
                  type="password"
                  value={registerData.password}
                  onChange={(e) => setRegisterData({ ...registerData, password: e.target.value })}
                />
              </div>
            </CardContent>
            <CardFooter className="justify-center">
              <Button onClick={handleRegister}>Register</Button>
            </CardFooter>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
