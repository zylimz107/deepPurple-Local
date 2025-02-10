package com.deeppurple.backend.config;


import com.deeppurple.backend.entity.EmotionCategory;
import com.deeppurple.backend.entity.Model;
import com.deeppurple.backend.entity.WordEmotionAssociation;
import com.deeppurple.backend.repository.EmotionCategoryRepository;
import com.deeppurple.backend.repository.ModelRepository;
import com.deeppurple.backend.repository.WordEmotionAssociationRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;


import java.io.IOException;
import java.util.Map;

@Component
public class DataSeeder {

    @Value("classpath:lexicon_nrc.json")
    private Resource nrcLexiconFile;

    @Value("classpath:lexicon_finance.json")
    private Resource financeLexiconFile;

    @Value("classpath:lexicon_social.json")
    private Resource socialLexiconFile;

    private final ModelRepository modelRepository;
    private final EmotionCategoryRepository emotionCategoryRepository;
    private final WordEmotionAssociationRepository wordRepository;
    private final ObjectMapper objectMapper;

    public DataSeeder(ModelRepository modelRepository,
                      EmotionCategoryRepository emotionCategoryRepository,
                      WordEmotionAssociationRepository wordRepository,
                      ObjectMapper objectMapper) {
        this.modelRepository = modelRepository;
        this.emotionCategoryRepository = emotionCategoryRepository;
        this.wordRepository = wordRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() throws IOException {
        if (modelRepository.count() == 0) {
            seedModelFromJson("GeneralModel", nrcLexiconFile);
            seedModelFromJson("FinanceModel", financeLexiconFile);
            seedModelFromJson("SocialModel", socialLexiconFile);
        }
    }

    private void seedModelFromJson(String modelName, Resource lexiconFile) throws IOException {
        Map<String, List<String>> lexiconData = objectMapper.readValue(lexiconFile.getInputStream(), Map.class);
        Model model = createModel(modelName, true, modelRepository);

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
        EmotionCategory category = emotionCategoryRepository.findByModelAndEmotion(model, emotion);
        if (category == null) {
            category = new EmotionCategory();
            category.setEmotion(emotion);
            category.setPredefined(true);
            category.setModel(model);
            category.setWordEmotionAssociations(new ArrayList<>());
            category = emotionCategoryRepository.save(category);
        }
        return category;
    }

    private void createWordEmotionAssociation(String word, EmotionCategory category) {
        WordEmotionAssociation association = new WordEmotionAssociation();
        association.setWord(word);
        association.setPredefined(true);
        association.setEmotionCategory(category);
        wordRepository.save(association);
    }
}
