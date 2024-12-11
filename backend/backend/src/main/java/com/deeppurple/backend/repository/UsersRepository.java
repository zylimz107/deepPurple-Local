package com.deeppurple.backend.repository;

import com.deeppurple.backend.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByUsername(String username);
}
