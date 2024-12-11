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
import { loginUser, registerUser } from "@/api";
import { useState } from "react";

export default function LoginRegister() {
  const [loginData, setLoginData] = useState({ username: "", password: "" });
  const [registerData, setRegisterData] = useState({ username: "", password: "" });


  // Login handler
  const handleLogin = async () => {
    try {
      const response = await loginUser(loginData.username, loginData.password);
    } catch (error) {
      console.log("error:", error.message);
    }
  };

  // Register handler
  const handleRegister = async () => {
    try {
      const response = await registerUser(registerData.username, registerData.password);
    } catch (error) {
      console.log("error", error.message);
    }
  };

  return (
    <div>
      <Tabs defaultValue="account" className="w-[400px]">
        <TabsList className="grid w-full grid-cols-2">
          <TabsTrigger value="login">Login</TabsTrigger>
          <TabsTrigger value="register">Register</TabsTrigger>
        </TabsList>
        <TabsContent value="login">
          <Card className="h-[310px]">
            <CardHeader>
              <CardTitle>Login</CardTitle>
              <CardDescription>For all users</CardDescription>
            </CardHeader>
            <CardContent className="space-y-2">
              <div className="space-y-1">
                <Label>Username</Label>
                <Input
                  id="login-username"
                  value={loginData.username}
                  onChange={(e) => setLoginData({ ...loginData, username: e.target.value })}
                  required
                />
              </div>
              <div className="space-y-1">
                <Label>Password</Label>
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
        <TabsContent value="register">
          <Card className="h-[310px]">
            <CardHeader>
              <CardTitle>Register Account</CardTitle>
              <CardDescription>If you do not have an account, register here</CardDescription>
            </CardHeader>
            <CardContent className="space-y-2">
              <div className="space-y-1">
                <Label>Username</Label>
                <Input
                  id="register-username"
                  value={registerData.username}
                  onChange={(e) => setRegisterData({ ...registerData, username: e.target.value })}
                />
              </div>
              <div className="space-y-1">
                <Label>Password</Label>
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
