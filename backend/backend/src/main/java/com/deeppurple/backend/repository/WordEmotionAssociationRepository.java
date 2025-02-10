package com.deeppurple.backend.repository;

import com.deeppurple.backend.entity.EmotionCategory;
import com.deeppurple.backend.entity.WordEmotionAssociation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WordEmotionAssociationRepository extends JpaRepository<WordEmotionAssociation, Long> {
    List<WordEmotionAssociation> findByEmotionCategory_ModelId(Long modelId);

    List<WordEmotionAssociation> findByEmotionCategoryIn(List<EmotionCategory> emotionCategories);

    Optional<WordEmotionAssociation> findByWordAndEmotionCategory(String word, EmotionCategory emotionCategory);

}
