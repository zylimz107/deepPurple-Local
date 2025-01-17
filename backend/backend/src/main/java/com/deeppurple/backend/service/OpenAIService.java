package com.deeppurple.backend.service;

import com.deeppurple.backend.entity.EmotionCategory;
import com.deeppurple.backend.entity.WordEmotionAssociation;
import com.deeppurple.backend.repository.ModelRepository;
import com.deeppurple.backend.repository.WordEmotionAssociationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OpenAIService {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    private final WebClient webClient;
    private final ModelRepository modelRepository;
    private final WordEmotionAssociationRepository wordEmotionAssociationRepository;

    public OpenAIService(ModelRepository modelRepository, WordEmotionAssociationRepository wordEmotionAssociationRepository) {
        this.modelRepository = modelRepository;
        this.wordEmotionAssociationRepository = wordEmotionAssociationRepository;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + System.getenv("OPENAI_API_KEY"))
                .build();
    }

    public Mono<Map<String, Object>> analyzeEmotionWithModel(String content, String modelName) {
        logger.info("Analyzing content '{}' with model '{}'", content, modelName);

        return Mono.justOrEmpty(modelRepository.findByName(modelName))
                .switchIfEmpty(Mono.error(new RuntimeException("Model not found: " + modelName)))
                .flatMap(model -> {
                    List<EmotionCategory> emotionCategories = model.getEmotionCategories();
                    if (emotionCategories.isEmpty()) {
                        return Mono.error(new RuntimeException("No emotion categories found for model: " + modelName));
                    }

                    // Fetch word-emotion associations for the model's emotion categories
                    List<WordEmotionAssociation> associations = wordEmotionAssociationRepository.findByEmotionCategoryIn(emotionCategories);
                    String prompt = createPrompt(content, emotionCategories, associations);
                    return callOpenAI(prompt);
                });
    }

    private String createPrompt(String content, List<EmotionCategory> emotionCategories, List<WordEmotionAssociation> associations) {
        // Build the list of emotions
        String emotionsList = emotionCategories.stream()
                .map(EmotionCategory::getEmotion)
                .reduce((a, b) -> a + ", " + b)
                .orElse("none");
        // Filter associations to include only words that appear in the content
        List<WordEmotionAssociation> filteredAssociations = associations.stream()
                .filter(association -> content.contains(association.getWord())) // Only include words that are in the content
                .collect(Collectors.toList());
        // Count emotions using the lexicon
        Map<String, Integer> emotionCounts = analyzeTextWithLexicon(content, filteredAssociations);


        // Convert emotion counts to string for prompt
        String emotionCountsString = emotionCounts.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .reduce((a, b) -> a + ", " + b)
                .orElse("none");

        // Build associated words for the prompt (words tied to emotions)
        String associatedWordsString = filteredAssociations.stream()
                .map(association -> association.getWord() + " (" + association.getEmotionCategory().getEmotion() + ")")
                .reduce((a, b) -> a + ", " + b)
                .orElse("none");

        logger.info(associatedWordsString);

        // Format the prompt for GPT
        String format = "{\n" +
                "  \"primaryEmotion\": {\n" +
                "    \"emotion\": \"joy\",\n" +
                "    \"percentage\": 40.50\n" +
                "  },\n" +
                "  \"secondaryEmotions\": [\n" +
                "    {\n" +
                "      \"emotion\": \"fear\",\n" +
                "      \"percentage\": 30.50\n" +
                "    },\n" +
                "    {\n" +
                "      \"emotion\": \"insecurity\",\n" +
                "      \"percentage\": 29.00\n" +
                "    }\n" +
                "  ],\n" +
                "  \"confidenceRating\": 75,\n" +
                "  \"summary\": \"The text contains the associated words \"awesome, incredible\", suggesting a feeling of joy, etc.\"\n" +
                "}";

        // Include lexicon emotion counts, associated words, and the content in the prompt
        return "Consider only the following emotions: [" + emotionsList + "] in the text: \"" + content + "\". "
                + "The content contains the following emotion counts based on the lexicon: " + emotionCountsString + ". "
                + "The following words are associated with emotions: " + associatedWordsString + ". "
                + "Respond with a JSON object containing: primaryEmotion with its percentage, secondaryEmotions with their percentages, "
                + "confidenceRating (out of 100), and a summary listing the associated words." + " Here's an example: \"" + format +"\"";
    }


    public Map<String, Integer> analyzeTextWithLexicon(String content, List<WordEmotionAssociation> associations) {
        Map<String, Integer> emotionCounts = new HashMap<>();

        // Initialize all emotions to 0
        for (String emotion : associations.stream().map(association -> association.getEmotionCategory().getEmotion()).distinct().toList()) {
            emotionCounts.put(emotion, 0);
        }

        // Tokenize input text (simple split by space)
        String[] words = content.toLowerCase().split("\\W+");

        // Check if each word is in the associations and update counts
        for (String word : words) {
            for (WordEmotionAssociation association : associations) {
                if (word.contains(association.getWord().toLowerCase())) {
                    String emotion = association.getEmotionCategory().getEmotion();
                    emotionCounts.put(emotion, emotionCounts.getOrDefault(emotion, 0) + 1);
                }
            }
        }

        return emotionCounts;
    }



    private Mono<Map<String, Object>> callOpenAI(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "max_tokens", 10000
        );

        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(this::processApiResponse)
                .doOnError(error -> {
                    if (error instanceof Throwable) {
                        logger.error("Error calling OpenAI API: {}", ((Throwable) error).getMessage());
                    } else {
                        logger.error("Error calling OpenAI API: Unknown error");
                    }
                });    }

    private Mono<Map<String, Object>> processApiResponse(Map<String, Object> response) {
        logger.info("API Response: {}", response);

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            String content = (String) message.get("content");

            if (content != null) {
                content = content.trim();
                if (content.startsWith("```json") && content.endsWith("```")) {
                    content = content.substring(8, content.length() - 3).trim();
                }

                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    return Mono.just(objectMapper.readValue(content, Map.class));
                } catch (JsonProcessingException e) {
                    return Mono.error(new RuntimeException("Error parsing JSON response: " + e.getMessage()));
                }
            } else {
                return Mono.error(new RuntimeException("Response content is empty"));
            }
        } else {
            return Mono.error(new RuntimeException("No choices found in API response"));
        }
    }
}
