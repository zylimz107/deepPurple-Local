package com.deeppurple.backend.service;

import com.deeppurple.backend.entity.EmotionCategory;
import com.deeppurple.backend.entity.WordEmotionAssociation;
import com.deeppurple.backend.repository.EmotionCategoryRepository;
import com.deeppurple.backend.repository.WordEmotionAssociationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WordEmotionAssociationService {

    @Autowired
    private WordEmotionAssociationRepository wordEmotionAssociationRepository;

    @Autowired
    private EmotionCategoryRepository emotionCategoryRepository;

    public WordEmotionAssociation associateWordWithEmotion(String word, Long emotionCategoryId) {
        EmotionCategory category = emotionCategoryRepository.findById(emotionCategoryId)
                .orElseThrow(() -> new RuntimeException("Emotion category not found"));

        if (category.isPredefined()) {
            throw new RuntimeException("Cannot assign to predefined categories");
        }

        WordEmotionAssociation association = new WordEmotionAssociation();
        association.setWord(word);
        association.setEmotionCategory(category);

        return wordEmotionAssociationRepository.save(association);
    }

    public void deleteWordEmotionAssociation(Long id) {
       WordEmotionAssociation word = wordEmotionAssociationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("word not found"));

        if (word.isPredefined()) {
            throw new RuntimeException("Cannot delete predefined words");
        }
        wordEmotionAssociationRepository.deleteById(id);
    }
}

