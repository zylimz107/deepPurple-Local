package com.deeppurple.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "word_emotion_associations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"word", "emotion_category_id"})
})

public class WordEmotionAssociation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String word;  // The word that is associated with an emotion

    private boolean predefined = false; // Indicates if this is a predefined model

    @ManyToOne
    @JoinColumn(name = "emotion_category_id")
    @JsonBackReference
    private EmotionCategory emotionCategory;  // The emotion category this word belongs to
}
