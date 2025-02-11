package com.deeppurple.backend.dto;

import com.deeppurple.backend.entity.WordEmotionAssociation;
import lombok.Data;

@Data
public class WordAssociationDTO {
    private Long id;
    private String word;
    private boolean predefined;
    private String emotion; // Directly extract the emotion

    public WordAssociationDTO(WordEmotionAssociation association) {
        this.id = association.getId();
        this.word = association.getWord();
        this.predefined = association.isPredefined();
        this.emotion = (association.getEmotionCategory() != null) ? association.getEmotionCategory().getEmotion() : "Unknown";
    }

    // Getters and Setters
}

