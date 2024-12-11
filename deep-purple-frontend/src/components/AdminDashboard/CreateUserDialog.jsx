import React, { useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogFooter,
} from "@/components/ui/dialog";
import {
  Select,
  SelectGroup,
  SelectValue,
  SelectTrigger,
  SelectContent,
  SelectItem,
} from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {createUser} from "../../api.jsx";

const CreateUserDialog = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const roles = ["user", "admin"];

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  const handleCreateUser = async () => {
    // Validate input fields
    if (!username || !password || !role) {
      alert("Please fill in all fields");
      return;
    }

    try {
      const response = await createUser(username, password, role);

      console.log(response);
      if (response.status == 201) {
        alert("user created successfully", response.data);
      }

      // Reset form state
      setUsername("");
      setPassword("");
      setRole("");
    } catch (error) {
      console.error(error);
      alert("An error occurred while creating the user");
    }
  };

  return (
    <>
      <Dialog>
        <DialogTrigger asChild>
          <Button variant="outline">Create User</Button>
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>Create User</DialogTitle>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="flex items-center gap-4">
              <Label htmlFor="username" className="text-right">
                Username
              </Label>
              <Input
                id="username"
                className="col-span-3"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Enter username"
              />
            </div>
            <div className="flex items-center gap-4">
              <Label htmlFor="password" className="text-right">
                Password
              </Label>
              <Input
                id="password"
                type={showPassword ? "text" : "password"}
                className="col-span-3"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter password"
              />
              <Button
                onClick={togglePasswordVisibility}
                style={{ padding: "8px" }}
              >
                {showPassword ? "Hide" : "Show"}
              </Button>
            </div>
            <div className="flex items-center gap-4">
              <Label htmlFor="role" className="text-right">
                Role
              </Label>
              <Select
                onValueChange={(value) => setRole(value)}
              >
                <SelectTrigger className="w-[180px]">
                  <SelectValue placeholder="Select a role"/>
                </SelectTrigger>
                <SelectContent>
                  <SelectGroup>
                    {roles.map((role) => (
                      <SelectItem key={role} value={role}>
                        {role}
                      </SelectItem>
                    ))}
                  </SelectGroup>
                </SelectContent>
              </Select>
            </div>
          </div>
          <DialogFooter>
            <Button type="button" onClick={handleCreateUser}>
              Create
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default CreateUserDialog;
