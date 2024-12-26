package com.deeppurple.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "emotion_categories", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"emotion", "model_id"})
})
public class EmotionCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String emotion; // e.g., "Happy", "Sad", etc.

    private boolean predefined = false; // Indicates if this is a predefined model

    @ManyToOne
    @JoinColumn(name = "model_id", nullable = false)
    @JsonBackReference
    private Model model;
}

