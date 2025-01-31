package com.deeppurple.backend.service;

import com.deeppurple.backend.entity.EmotionCategory;
import com.deeppurple.backend.entity.Model;
import com.deeppurple.backend.entity.WordEmotionAssociation;
import com.deeppurple.backend.repository.ModelRepository;
import com.deeppurple.backend.repository.WordEmotionAssociationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class MissingEmotionService {
    private final WordEmotionAssociationRepository wordEmotionAssociationRepository;
    private final ModelRepository modelRepository;
    private final WebClient webClient;
    private final Logger logger = LoggerFactory.getLogger(MissingEmotionService.class);

    @Autowired
    public MissingEmotionService(WordEmotionAssociationRepository wordEmotionAssociationRepository, ModelRepository modelRepository) {
        this.wordEmotionAssociationRepository = wordEmotionAssociationRepository;
        this.modelRepository = modelRepository;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + System.getenv("OPENAI_API_KEY"))
                .build();
    }

    public Mono<Void> processMissingEmotions(String content, String modelName) {
        logger.info("Processing missing emotions for model '{}'", modelName);

        return Mono.justOrEmpty(modelRepository.findByName(modelName))
                .switchIfEmpty(Mono.error(new RuntimeException("Model not found: " + modelName)))
                .flatMap(model -> {
                    List<EmotionCategory> emotionCategories = model.getEmotionCategories();
                    if (emotionCategories.isEmpty()) {
                        return Mono.error(new RuntimeException("No emotion categories found for model: " + modelName));
                    }

                    List<WordEmotionAssociation> existingAssociations = wordEmotionAssociationRepository.findByEmotionCategoryIn(emotionCategories);
                    Set<String> knownWords = existingAssociations.stream()
                            .map(WordEmotionAssociation::getWord)
                            .collect(Collectors.toSet());

                    Set<String> wordsInContent = extractWordsFromContent(content);
                    wordsInContent.removeAll(knownWords); // Filter out known words

                    if (wordsInContent.isEmpty()) {
                        logger.info("No new words or emojis found.");
                        return Mono.empty();
                    }
                    logger.info("processing emotions '{}'.", wordsInContent);

                    String prompt = createEmotionClassificationPrompt(wordsInContent, emotionCategories);
                    logger.info("calling openAI for emotion classification.");
                    return callOpenAIForEmotionClassification(prompt)
                            .flatMap(newAssociations -> saveNewAssociations(newAssociations, model));
                });
    }

    private Set<String> extractWordsFromContent(String content) {
        Set<String> words = new HashSet<>();
        Matcher matcher = Pattern.compile("\\b\\w+\\b|\\p{So}|\\p{Cs}").matcher(content);
        while (matcher.find()) {
            words.add(matcher.group());
        }
        return words;
    }

    private String createEmotionClassificationPrompt(Set<String> words, List<EmotionCategory> emotionCategories) {
        String emotionList = emotionCategories.stream()
                .map(EmotionCategory::getEmotion)
                .collect(Collectors.joining(", "));

        String wordsList = String.join(", ", words);

        return "Analyze and identify the most appropriate emotion for each of the following abbreviations, words or emojis based on the given emotions: " + emotionList +
                ".\nWords/Emojis: " + wordsList +
                ".\nIgnore common stop words (e.g., \"and\", \"the\", \"me\", \"also\", \"my\", \"with\") and numbers." +
                ".\nOnly return words, emojis, or abbreviations that have an emotional association. "+
                "\nFormat your response as a JSON array: [{\"word\": \"word1\", \"emotion\": \"emotion1\"}, {\"word\": \"word2\", \"emotion\": \"emotion2\"}].";
    }

    private Mono<List<Map<String, String>>> callOpenAIForEmotionClassification(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "max_tokens", 5000
        );

        return webClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> {
                    JsonNode choices = response.path("choices");
                    if (choices.isArray() && choices.size() > 0) {
                        String jsonResponse = choices.get(0).path("message").path("content").asText();
                        logger.info("jsonResponse: {}", jsonResponse);

                        try {
                            // Strip out any extra characters (like backticks) around the JSON response
                            jsonResponse = jsonResponse.replace("```json", "").replace("```", "").trim();

                            ObjectMapper objectMapper = new ObjectMapper();

                            // Step 1: Parse the jsonResponse string into a JsonNode
                            JsonNode parsedJson = objectMapper.readTree(jsonResponse);

                            // Step 2: Check if parsedJson is actually an array before proceeding
                            if (parsedJson.isArray()) {
                                // Step 3: Convert the parsed JSON array into a List of Maps
                                return objectMapper.convertValue(parsedJson, new TypeReference<List<Map<String, String>>>() {});
                            } else {
                                throw new RuntimeException("Parsed JSON is not an array.");
                            }
                        } catch (Exception e) {
                            logger.error("Error parsing OpenAI response", e);
                            throw new RuntimeException("Error parsing OpenAI response", e);
                        }
                    }
                    return List.of(); // Return an empty list if there's no valid choices
                });



    }

    private Mono<Void> saveNewAssociations(List<Map<String, String>> newAssociations, Model model) {
        List<WordEmotionAssociation> associationsToSave = newAssociations.stream()
                .map(entry -> {
                    String word = entry.get("word");
                    String emotion = entry.get("emotion");
                    EmotionCategory category = model.getEmotionCategories().stream()
                            .filter(ec -> ec.getEmotion().equalsIgnoreCase(emotion))
                            .findFirst()
                            .orElse(null);
                    if (category != null) {
                        WordEmotionAssociation association = new WordEmotionAssociation();
                        association.setWord(word);
                        association.setEmotionCategory(category);
                        return association;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (associationsToSave.isEmpty()) {
            return Mono.empty();
        }

        return Mono.fromRunnable(() -> wordEmotionAssociationRepository.saveAll(associationsToSave))
                .then();
    }
}


