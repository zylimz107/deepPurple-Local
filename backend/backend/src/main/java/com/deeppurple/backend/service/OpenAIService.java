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

import java.util.List;
import java.util.Map;

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

    @Cacheable(value = "emotionCache", key = "#content + #modelName")
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

        // Check if any of the words from associations appear in the content
        String matchedWords = associations.stream()
                .filter(association -> content.contains(association.getWord()))  // Check if the word is present in the content
                .map(association -> association.getWord() + " (" + association.getEmotionCategory().getEmotion() + ")")
                .reduce((a, b) -> a + ", " + b)
                .orElse("none");

        String format = "{\n" +
                "  \"primaryEmotion\": {\n" +
                "    \"emotion\": \"joy\",\n" +
                "    \"percentage\": 40\n" +
                "  },\n" +
                "  \"secondaryEmotions\": [\n" +
                "    {\n" +
                "      \"emotion\": \"fear\",\n" +
                "      \"percentage\": 30\n" +
                "    },\n" +
                "    {\n" +
                "      \"emotion\": \"insecurity\",\n" +
                "      \"percentage\": 30\n" +
                "    }\n" +
                "  ],\n" +
                "  \"confidenceRating\": 75,\n" +
                "  \"summary\": \"The text contains the associated words \"awesome, incredible\", suggesting a feeling of joy, etc.\"\n" +
                "}";

        return "Consider only the following emotions: [" + emotionsList + "] in the text: \"" + content + "\". "
                + "The content contains the following words associated with emotions: " + matchedWords + ". "
                + "Respond with a JSON object containing: primaryEmotion with its percentage, secondaryEmotions with their percentages, "
                + "confidenceRating (out of 100), and a summary describing the associated words if any." + " Here's an example: \"" + format +"\"";
    }

    private Mono<Map<String, Object>> callOpenAI(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "max_tokens", 1000
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
