package com.deeppurple.backend.controller;

import com.deeppurple.backend.entity.Users;
import com.deeppurple.backend.service.UsersService;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Users user) {
        try {
            Users registeredUser = usersService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error registering user: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            Users user = usersService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
        }
    }




}


@Getter
@Setter
class LoginRequest {
    @NotEmpty(message = "username cannot be empty")
    private String username;
    @NotEmpty(message = "password cannot be empty")
    private String password;

}
