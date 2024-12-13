import React, { useState, useEffect } from "react"
import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import UpdateUserDialog from "./UpdateUserDialog";
import { Button } from "@/components/ui/button";
import { getAllUsers, deleteUser } from "../../api.jsx";
import { Eye, EyeOff, MoreHorizontal, Trash2, Pencil } from 'lucide-react';

const UsersTable = () => {
  const [users, setUsers] = useState([]);
  const [visiblePasswords, setVisiblePasswords] = useState({});
  const [selectedUserID, setSelectedUserID] = useState(null);
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const fetchUsers = async () => {
    try {
      const response = await getAllUsers()
      setUsers(response.data)
    } catch (error) {
      console.error("Failed to fetch users:", error)
    }
  }

  useEffect(() => {
    fetchUsers()
  }, [])

  const togglePasswordVisibility = (userId: number) => {
    setVisiblePasswords(prev => ({
      ...prev,
      [userId]: !prev[userId]
    }))
  }

  const handleOpenUpdateDialog = (userID: number) => {
    setSelectedUserID(userID);
    setIsDialogOpen(true);
  }

  const handleUserUpdate = (updatedUser) => {
    setUsers((prevUsers) =>
      prevUsers.map((user) =>
        user.id === updatedUser.id ? updatedUser : user
      )
    );
  };

  const handleDeleteUser = async (userID: number) => {
      try {
        deleteUser(userID);
        alert("User deleted successfully");
      } catch (error) {
        console.error("Failed to delete user:", error);
        alert("Failed to delete user.");
      }
    await fetchUsers();
   }

  return (
    <>
      <Table className="w-[750px]">
        <TableCaption>List of Users</TableCaption>
        <TableHeader>
          <TableRow>
            <TableHead>ID</TableHead>
            <TableHead>Username</TableHead>
            <TableHead>Password</TableHead>
            <TableHead>Role</TableHead>
            <TableHead className="text-right">Actions</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {users.map((user) => (
            <TableRow key={user.id}>
              <TableCell>{user.id}</TableCell>
              <TableCell>{user.username}</TableCell>
              <TableCell>
                {visiblePasswords[user.id] ? user.password : '••••••••'}
                <Button
                  variant="ghost"
                  size="icon"
                  onClick={() => togglePasswordVisibility(user.id)}
                >
                  {visiblePasswords[user.id] ? (
                    <EyeOff className="h-4 w-4" />
                  ) : (
                    <Eye className="h-4 w-4" />
                  )}
                  <span className="sr-only">
                    {visiblePasswords[user.id] ? "Hide" : "Show"} password
                  </span>
                </Button>
              </TableCell>
              <TableCell>{user.role}</TableCell>
              <TableCell className="text-right">
                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <Button variant="ghost" className="h-8 w-8 p-0">
                      <span className="sr-only">Open menu</span>
                      <MoreHorizontal className="h-4 w-4" />
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end">
                    <DropdownMenuItem
                      onSelect={() => handleOpenUpdateDialog(user.id)}
                    >
                     <Pencil className="mr-2 h-4 w-4" />
                      Update
                    </DropdownMenuItem>
                    <DropdownMenuItem onSelect={() => handleDeleteUser(user.id)}>
                      <Trash2 className="mr-2 h-4 w-4" />
                      <span>Delete</span>
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      <UpdateUserDialog
        open={isDialogOpen}
        onOpenChange={setIsDialogOpen}
        userID={selectedUserID || undefined}
        onUserUpdated={handleUserUpdate}
      />
    </>
  )
}

export default UsersTable;