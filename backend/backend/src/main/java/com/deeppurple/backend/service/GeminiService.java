package com.deeppurple.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {
    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);
    private final WebClient webClient;

    public GeminiService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofSeconds(10)) // Set timeout as needed
                ))
                .build();
    }

    public Mono<Map<String, Object>> analyzeEmotionWithGemini(String prompt) {
        logger.info("Calling Gemini API with prompt.");


        String apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("API Key is missing");
        }

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of(
                        "temperature", 1,
                        "top_p", 0.95,
                        "top_k", 40,
                        "max_output_tokens", 8192
                )
        );

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/v1beta/models/gemini-1.5-flash:generateContent")
                        .queryParam("key", apiKey)
                        .build())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(this::processApiResponse)
                .doOnError(error -> {
                    if (error instanceof Throwable) {
                        logger.error("Error calling GeminiAI API: {}", ((Throwable) error).getMessage());
                    } else {
                        logger.error("Unexpected error calling GeminiAI API: {}", error.toString());
                    }
                });
    }

    private Mono<Map<String, Object>> processApiResponse(Map<String, Object> response) {
        logger.info("Gemini API Response: {}", response);

        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return Mono.error(new RuntimeException("No candidates found in Gemini API response"));
            }

            Map<String, Object> firstCandidate = candidates.get(0);
            Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

            if (parts == null || parts.isEmpty()) {
                return Mono.error(new RuntimeException("No parts found in Gemini response content"));
            }

            // Extract the "text" field, which contains the actual JSON response
            String jsonText = (String) parts.get(0).get("text");

            // Remove potential Markdown formatting (e.g., ```json ... ```)
            if (jsonText.startsWith("```json")) {
                jsonText = jsonText.replace("```json", "").replace("```", "").trim();
            }

            // Parse the extracted JSON string into a Map
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> parsedResponse = objectMapper.readValue(jsonText, new TypeReference<>() {});

            return Mono.just(parsedResponse);
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Error parsing Gemini API response: " + e.getMessage(), e));
        }
    }

}
