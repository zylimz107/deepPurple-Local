package com.deeppurple.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.deeppurple.backend.entity.Model;

import java.util.Optional;

public interface ModelRepository extends JpaRepository<Model, Long> {
    // Custom query to find a model by its name
    Optional<Model> findByName(String name);
}
