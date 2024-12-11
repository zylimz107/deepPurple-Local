package com.deeppurple.backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "models")
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g., "Model A", "Model B", "Model C"

    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<EmotionCategory> emotionCategories = new ArrayList<>(); // Initialize as empty list
}

