import React, { useState, useEffect } from "react"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
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
import { getUserByID, updateUser } from "../../api.jsx";

interface UpdateUserDialogProps {
  userID?: number;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onUserUpdated: (updatedUser: any) => void; // New prop
}

const UpdateUserDialog: React.FC<UpdateUserDialogProps> = ({
  userID,
  open,
  onOpenChange,
  onUserUpdated,
}) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const roles = ["user", "admin"];

  useEffect(() => {
    const fetchUser = async () => {
      if (userID) {
        try {
          const { data: user } = await getUserByID(userID);
          console.log("Fetched user:", user);
          setUsername(user.username);
          setPassword(user.password);
          setRole(user.role);
        } catch (error) {
          console.error("Failed to fetch user:", error);
        }
      }
    };

    if (open) {
      fetchUser();
    }
  }, [userID, open]);


  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  const handleUpdate = async () => {
    if (!userID) return;

    setLoading(true);
    try {
      const updatedUser = { id: userID, username, password, role }; // Include the `id`
      await updateUser(userID, updatedUser);
      alert("User updated successfully!");

      // Notify parent component about the update
      onUserUpdated(updatedUser);

      onOpenChange(false); // Close the dialog
    } catch (error) {
      console.error("Failed to update user:", error);
      alert("Failed to update user.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Update User</DialogTitle>
          <DialogDescription>
            Make changes to the user account here. Click save when you're done.
          </DialogDescription>
        </DialogHeader>
        <div className="grid gap-4 py-4">
          <div className="flex items-center gap-4">
            <Label htmlFor="username" className="text-right w-20">
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
            <Label htmlFor="password" className="text-right w-20">
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
              variant="outline"
              size="sm"
            >
              {showPassword ? "Hide" : "Show"}
            </Button>
          </div>
          <div className="flex items-center gap-4">
            <Label htmlFor="role" className="text-right w-20">
              Role
            </Label>
            <Select onValueChange={setRole} value={role}>
              <SelectTrigger className="w-[180px]">
                <SelectValue placeholder="Select a role" />
              </SelectTrigger>
              <SelectContent>
                <SelectGroup>
                  {roles.map((r) => (
                    <SelectItem key={r} value={r}>
                      {r}
                    </SelectItem>
                  ))}
                </SelectGroup>
              </SelectContent>
            </Select>
          </div>
        </div>
        <DialogFooter>
          <Button type="button" onClick={handleUpdate} disabled={loading}>
            {loading ? "Saving..." : "Save changes"}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default UpdateUserDialog;