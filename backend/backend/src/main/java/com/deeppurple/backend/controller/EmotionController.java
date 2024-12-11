package com.deeppurple.backend.controller;

import com.deeppurple.backend.entity.EmotionCategory;
import com.deeppurple.backend.entity.WordEmotionAssociation;
import com.deeppurple.backend.repository.WordEmotionAssociationRepository;
import com.deeppurple.backend.service.EmotionCategoryService;
import com.deeppurple.backend.service.WordEmotionAssociationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emotion")
public class EmotionController {

    @Autowired
    private EmotionCategoryService emotionCategoryService;

    @Autowired
    private WordEmotionAssociationService wordEmotionAssociationService;

    @Autowired
    private WordEmotionAssociationRepository wordEmotionAssociationRepository;

    // Emotion Category Endpoints
    @PostMapping("/category")
    public EmotionCategory createEmotionCategory(
            @RequestParam Long modelId,
            @RequestParam String name) {
        return emotionCategoryService.addEmotionCategory(modelId, name);
    }

    @PutMapping("/category/{id}")
    public EmotionCategory updateEmotionCategory(
            @PathVariable Long id,
            @RequestParam String name) {
        return emotionCategoryService.updateEmotionCategory(id, name);
    }

    @DeleteMapping("/category/{id}")
    public void deleteEmotionCategory(@PathVariable Long id) {
        emotionCategoryService.deleteEmotionCategory(id);
    }

    @GetMapping("/category")
    public List<EmotionCategory> getEmotionCategoriesByModel(@RequestParam Long modelId) {
        return emotionCategoryService.getEmotionCategoriesByModel(modelId);
    }

    // Word-Emotion Association Endpoints
    @PostMapping("/word-association")
    public WordEmotionAssociation createWordEmotionAssociation(
            @RequestParam String word,
            @RequestParam Long emotionCategoryId) {
        return wordEmotionAssociationService.associateWordWithEmotion(word, emotionCategoryId);
    }

    @GetMapping("/word-associations/{modelId}")
    public List<WordEmotionAssociation> getAssociationsByModel(@PathVariable Long modelId) {
        return wordEmotionAssociationRepository.findByEmotionCategory_ModelId(modelId);
    }


    @DeleteMapping("/word-association/{id}")
    public void deleteWordEmotionAssociation(@PathVariable Long id) {
        wordEmotionAssociationService.deleteWordEmotionAssociation(id);
    }
}
