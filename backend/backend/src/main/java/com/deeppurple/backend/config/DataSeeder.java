package com.deeppurple.backend.config;


import com.deeppurple.backend.entity.EmotionCategory;
import com.deeppurple.backend.entity.Model;
import com.deeppurple.backend.entity.WordEmotionAssociation;
import com.deeppurple.backend.repository.EmotionCategoryRepository;
import com.deeppurple.backend.repository.ModelRepository;
import com.deeppurple.backend.repository.WordEmotionAssociationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder {

    @Bean
    public CommandLineRunner seedData(ModelRepository modelRepository,
                                      EmotionCategoryRepository emotionCategoryRepository,
                                      WordEmotionAssociationRepository wordRepository) {
        return args -> {
            if (modelRepository.count() == 0) { // Only seed data if the database is empty
                seedModels(modelRepository, emotionCategoryRepository, wordRepository);
            }
        };
    }

    private void seedModels(ModelRepository modelRepository,
                            EmotionCategoryRepository emotionCategoryRepository,
                            WordEmotionAssociationRepository wordRepository) {
        // Create predefined models
        Model sentimentPulse = createModel("SentimentPulse", true, modelRepository);
        Model empathyGauge = createModel("EmpathyGauge", true, modelRepository);
        Model engageScope = createModel("EngageScope", true, modelRepository);
        Model relationshipInsight = createModel("RelationshipInsight", true, modelRepository);
        Model socialTone = createModel("SocialTone", true, modelRepository);

        // Add emotion categories and words for each model
        addEmotionCategory(sentimentPulse, "Positive", true, Arrays.asList("happy", "pleased", "excellent"), emotionCategoryRepository, wordRepository);
        addEmotionCategory(sentimentPulse, "Neutral", true, Arrays.asList("okay", "fine", "average"), emotionCategoryRepository, wordRepository);
        addEmotionCategory(sentimentPulse, "Negative", true, Arrays.asList("angry", "terrible", "poor"), emotionCategoryRepository, wordRepository);

        addEmotionCategory(empathyGauge, "Calm", true, Arrays.asList("peaceful", "secure", "relaxed"), emotionCategoryRepository, wordRepository);
        addEmotionCategory(empathyGauge, "Anxious", true, Arrays.asList("worried", "tense", "nervous"), emotionCategoryRepository, wordRepository);
        addEmotionCategory(empathyGauge, "Depressed", true, Arrays.asList("unhappy", "down", "defeated", "grief"), emotionCategoryRepository, wordRepository);

        addEmotionCategory(engageScope, "Motivated", true, Arrays.asList("driven", "focused", "energetic"), emotionCategoryRepository, wordRepository);
        addEmotionCategory(engageScope, "Disconnected", true, Arrays.asList("bored", "detached", "apathetic"), emotionCategoryRepository, wordRepository);
        addEmotionCategory(engageScope, "Frustrated", true, Arrays.asList("stressed", "annoyed", "angry", "upset"), emotionCategoryRepository, wordRepository);

        addEmotionCategory(relationshipInsight, "Affectionate", true, Arrays.asList("kind", "caring", "thoughtful"), emotionCategoryRepository, wordRepository);
        addEmotionCategory(relationshipInsight, "Conflicted", true, Arrays.asList("uncertain", "hesitant", "torn"), emotionCategoryRepository, wordRepository);
        addEmotionCategory(relationshipInsight, "Hostile", true, Arrays.asList("hateful", "bitter", "vengeful", "annoyed"), emotionCategoryRepository, wordRepository);

        addEmotionCategory(socialTone, "Excited", true, Arrays.asList("amazing", "hyped", "trending"), emotionCategoryRepository, wordRepository);
        addEmotionCategory(socialTone, "Critical", true, Arrays.asList("boring", "fake", "annoying"), emotionCategoryRepository, wordRepository);
        addEmotionCategory(socialTone, "Supportive", true, Arrays.asList("love", "agree", "inspiring", "uplifting"), emotionCategoryRepository, wordRepository);

    }

    private Model createModel(String name, boolean predefined, ModelRepository modelRepository) {
        Model model = new Model();
        model.setName(name);
        model.setPredefined(predefined);
        return modelRepository.save(model);
    }

    private void addEmotionCategory(Model model, String emotion, boolean predefined, List<String> words,
                                    EmotionCategoryRepository emotionCategoryRepository,
                                    WordEmotionAssociationRepository wordRepository) {
        EmotionCategory category = new EmotionCategory();
        category.setEmotion(emotion);
        category.setPredefined(predefined);
        category.setModel(model);

        EmotionCategory savedCategory = emotionCategoryRepository.save(category);

        words.forEach(word -> {
            WordEmotionAssociation association = new WordEmotionAssociation();
            association.setWord(word);
            association.setPredefined(predefined);
            association.setEmotionCategory(savedCategory);
            wordRepository.save(association);
        });
    }
}
