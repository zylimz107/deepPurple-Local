package com.deeppurple.backend.repository;

import com.deeppurple.backend.entity.EmotionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmotionCategoryRepository extends JpaRepository<EmotionCategory, Long> {
    List<EmotionCategory> findByModelId(Long modelId);
}
