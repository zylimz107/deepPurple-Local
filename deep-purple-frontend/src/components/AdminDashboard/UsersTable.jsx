import React, { useState, useEffect } from "react"
import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { Button } from "@/components/ui/button"
import { getAllUsers } from "../../api.jsx"
import { Eye, EyeOff } from 'lucide-react'

const UsersTable = () => {
  const [users, setUsers] = useState([]);
  const [visiblePasswords, setVisiblePasswords] = useState({})

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await getAllUsers()
        setUsers(response.data)
      } catch (error) {
        console.error("Failed to fetch users:", error)
      }
    }
    fetchUsers()
  }, [])

  const togglePasswordVisibility = (userId: number) => {
    setVisiblePasswords(prev => ({
      ...prev,
      [userId]: !prev[userId]
    }))
  }

  return (
    <Table>
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

            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  )
}

export default UsersTable;