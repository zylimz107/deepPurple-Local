package com.deeppurple.backend.controller;

import com.deeppurple.backend.entity.Users;
import com.deeppurple.backend.service.UsersService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsersService usersService;

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody Users user) {
        try {
            Users registeredUser = usersService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating user: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = usersService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody Users updatedUser) {
        try {
            Users updated = usersService.updateUser(id, updatedUser);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        try {
            usersService.deleteUser(id);
            return ResponseEntity.ok("User Deleted Successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
