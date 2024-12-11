package com.deeppurple.backend.service;

import com.deeppurple.backend.entity.Users;
import com.deeppurple.backend.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;

    public Users createUser(Users user) {
        if (user.getRole() == null) {
            user.setRole("user");
        }
        return usersRepository.save(user);
    }

    public Users loginUser(String username, String password) {
        Users user = usersRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Incorrect username or password");
        }
        return user;
    }

    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    public Users updateUser(Long id, Users updatedUser) {
        Optional<Users> currentUser = usersRepository.findById(id);
        if (currentUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        currentUser.get().setUsername(updatedUser.getUsername());
        currentUser.get().setPassword(updatedUser.getPassword());
        currentUser.get().setRole(updatedUser.getRole());
        return usersRepository.save(currentUser.get());
    }

    public void deleteUser(Long id) {
        if (usersRepository.existsById(id)){
            usersRepository.deleteById(id);
        }
    }
}
