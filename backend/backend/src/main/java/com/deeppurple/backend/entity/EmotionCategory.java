package com.deeppurple.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
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

    @OneToMany(mappedBy = "emotionCategory", cascade = {CascadeType.ALL, CascadeType.REMOVE}, orphanRemoval = true)
    @JsonManagedReference
    private List<WordEmotionAssociation> wordEmotionAssociations = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "model_id", nullable = false)
    @JsonBackReference
    private Model model;
}

