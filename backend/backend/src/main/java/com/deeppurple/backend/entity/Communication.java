package com.deeppurple.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Entity
@Table(name = "communications")
@Data
public class Communication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10485760)
    private String content;

    @ElementCollection
    @CollectionTable(name = "communication_secondary_emotions", joinColumns = @JoinColumn(name = "communication_id"))
    private List<EmotionDetails> secondaryEmotions;

    @Embedded
    private EmotionDetails primaryEmotion;

    @Column(length = 1000)
    private String summary;
    private int confidenceRating;

    private String modelName;  // The model used for analysis

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @OneToMany
    @JoinColumn(name = "communication_id")
    private List<WordEmotionAssociation> wordAssociations;  // New field for word associations

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now(); // Set timestamp when communication is created
    }
}
