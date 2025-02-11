package com.deeppurple.backend.service;

import com.deeppurple.backend.entity.EmotionCategory;
import com.deeppurple.backend.entity.Model;
import com.deeppurple.backend.repository.EmotionCategoryRepository;
import com.deeppurple.backend.repository.ModelRepository;
import com.deeppurple.backend.repository.WordEmotionAssociationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmotionCategoryService {

    @Autowired
    private EmotionCategoryRepository emotionCategoryRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private WordEmotionAssociationRepository wordEmotionAssociationRepository;

    // Add a new emotion category associated with a specific model
    public EmotionCategory addEmotionCategory(Long modelId, String name) {
        // Validate the name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Emotion category name cannot be null or empty");
        }

        // Find the model
        Model model = modelRepository.findById(modelId)
                .orElseThrow(() -> new RuntimeException("Model not found"));

        if (model.isPredefined()) {
            throw new RuntimeException("Cannot assign to predefined models");
        }

        // Create a new emotion category
        EmotionCategory category = new EmotionCategory();
        category.setEmotion(name);
        category.setModel(model); // Associate with the model

        // Ensure bidirectional consistency
        model.getEmotionCategories().add(category);

        // Save the emotion category directly
        emotionCategoryRepository.save(category);

        return category;  // Return the saved category
    }



    // Update an existing emotion category
    public EmotionCategory updateEmotionCategory(Long id, String name) {
        // Find the category
        EmotionCategory category = emotionCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emotion category not found"));

        if (category.isPredefined()) {
            throw new RuntimeException("Cannot modify predefined emotion categories");
        }

        // Update the emotion name
        category.setEmotion(name);

        return emotionCategoryRepository.save(category);
    }

    // Delete an emotion category
    @Transactional
    public void deleteEmotionCategory(Long id) {
        EmotionCategory category = emotionCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        wordEmotionAssociationRepository.deleteAll(category.getWordEmotionAssociations()); // Delete associations
        emotionCategoryRepository.delete(category); // Now delete category
    }


    // Get all emotion categories for a specific model
    public List<EmotionCategory> getEmotionCategoriesByModel(Long modelId) {
        // Ensure the model exists
        if (!modelRepository.existsById(modelId)) {
            throw new RuntimeException("Model not found");
        }

        // Retrieve all categories associated with the model
        return emotionCategoryRepository.findByModelId(modelId);
    }
}
