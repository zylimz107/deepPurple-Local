package com.deeppurple.backend.config;


import com.deeppurple.backend.entity.EmotionCategory;
import com.deeppurple.backend.entity.Model;
import com.deeppurple.backend.entity.WordEmotionAssociation;
import com.deeppurple.backend.repository.EmotionCategoryRepository;
import com.deeppurple.backend.repository.ModelRepository;
import com.deeppurple.backend.repository.WordEmotionAssociationRepository;
import org.springframework.stereotype.Component;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

@Component
public class DataSeeder {

    @Value("classpath:lexicon.json") // The path to your JSON file
    private Resource lexiconFile;

    private final ModelRepository modelRepository;
    private final EmotionCategoryRepository emotionCategoryRepository;
    private final WordEmotionAssociationRepository wordRepository;

    public DataSeeder(ModelRepository modelRepository,
                      EmotionCategoryRepository emotionCategoryRepository,
                      WordEmotionAssociationRepository wordRepository) {
        this.modelRepository = modelRepository;
        this.emotionCategoryRepository = emotionCategoryRepository;
        this.wordRepository = wordRepository;
    }

    @PostConstruct
    public void init() throws IOException {
        if (modelRepository.count() == 0) {
            seedModelsFromJson();
        }
    }

    private void seedModelsFromJson() throws IOException {
        // Read the JSON file and map it to a Map<String, List<String>> structure
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<String>> lexiconData = objectMapper.readValue(lexiconFile.getInputStream(), Map.class);

        // Assuming you have at least one predefined model for association
        Model model = createModel("NRCModel", true, modelRepository);

        // Loop through the words in the lexicon and associate them with emotion categories
        lexiconData.forEach((word, emotions) -> {
            emotions.forEach(emotion -> {
                EmotionCategory category = findOrCreateEmotionCategory(model, emotion);
                createWordEmotionAssociation(word, category);
            });
        });
    }

    private Model createModel(String name, boolean predefined, ModelRepository modelRepository) {
        Model model = new Model();
        model.setName(name);
        model.setPredefined(predefined);
        return modelRepository.save(model);
    }

    private EmotionCategory findOrCreateEmotionCategory(Model model, String emotion) {
        // Try to find the emotion category in the database
        EmotionCategory category = emotionCategoryRepository.findByModelAndEmotion(model, emotion);
        if (category == null) {
            // If it doesn't exist, create a new one
            category = new EmotionCategory();
            category.setEmotion(emotion);
            category.setPredefined(true); // Assuming predefined here, you can modify this
            category.setModel(model);
            category = emotionCategoryRepository.save(category);
        }
        return category;
    }

    private void createWordEmotionAssociation(String word, EmotionCategory category) {
        // Create the word-emotion association
        WordEmotionAssociation association = new WordEmotionAssociation();
        association.setWord(word);
        association.setPredefined(true); // Assuming predefined here, you can modify this
        association.setEmotionCategory(category);
        wordRepository.save(association);
    }
}

