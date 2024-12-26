package com.deeppurple.backend.service;

import com.deeppurple.backend.entity.EmotionCategory;
import com.deeppurple.backend.entity.Model;
import com.deeppurple.backend.repository.EmotionCategoryRepository;
import com.deeppurple.backend.repository.ModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmotionCategoryService {

    @Autowired
    private EmotionCategoryRepository emotionCategoryRepository;

    @Autowired
    private ModelRepository modelRepository;

    // Add a new emotion category associated with a specific model
    public EmotionCategory addEmotionCategory(Long modelId, String name) {
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

        // Update the model's emotionCategories list
        if (model.getEmotionCategories() == null) {
            model.setEmotionCategories(new ArrayList<>());
        }
        model.getEmotionCategories().add(category);

        // Save the emotion category
        emotionCategoryRepository.save(category);

        // Save the updated model
        modelRepository.save(model);

        return category;
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
    public void deleteEmotionCategory(Long id) {
        EmotionCategory category = emotionCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emotion category not found"));

        if (category.isPredefined()) {
            throw new RuntimeException("Cannot delete predefined emotion categories");
        }
        emotionCategoryRepository.deleteById(id);
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
