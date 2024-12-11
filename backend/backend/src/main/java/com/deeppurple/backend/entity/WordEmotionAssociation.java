package com.deeppurple.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "word_emotion_associations")
@Data
public class WordEmotionAssociation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String word;  // The word that is associated with an emotion

    @ManyToOne
    @JoinColumn(name = "emotion_category_id")
    private EmotionCategory emotionCategory;  // The emotion category this word belongs to
}
