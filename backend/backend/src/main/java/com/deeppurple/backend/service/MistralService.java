package com.deeppurple.backend.service;

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
public class MistralService {
    private static final Logger logger = LoggerFactory.getLogger(MistralService.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson for JSON parsing


    public MistralService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.mistral.ai")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + System.getenv("MISTRAL_API_KEY"))
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofSeconds(10)) // Set timeout as needed
                ))
                .build();
    }

    public Mono<Map<String, Object>> analyzeWithMistral(String prompt) {
        logger.info("Calling Mistral API with prompt.");

        Map<String, Object> requestBody = Map.of(
                "model", "mistral-small-latest",
                "messages", List.of(Map.of(
                        "role", "user",
                        "content", prompt
                ))
        );

        return webClient.post()
                .uri("/v1/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(this::processApiResponse)
                .doOnError(error -> {
                    if (error instanceof Throwable) {
                        logger.error("Error calling Mistral API: {}", ((Throwable) error).getMessage());
                    } else {
                        logger.error("Unexpected error calling Mistral API: {}", error.toString());
                    }
                });
    }

    private Mono<Map<String, Object>> processApiResponse(Map<String, Object> response) {
        logger.info("Mistral API Response: {}", response);

        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices == null || choices.isEmpty()) {
                return Mono.error(new RuntimeException("No choices found in Mistral API response"));
            }

            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            String content = (String) message.get("content");

            // Remove Markdown formatting (` ```json ... ``` `)
            if (content.startsWith("```json")) {
                content = content.replaceAll("```json|```", "").trim();
            }
            // Remove backticks if present
            if (content.startsWith("```") && content.endsWith("```")) {
                content = content.substring(3, content.length() - 3);  // Strip off the backticks
            }

            // Parse the cleaned JSON string into a Map
            Map<String, Object> parsedContent = objectMapper.readValue(content, Map.class);

            // Return the parsed JSON as the response
            return Mono.just(parsedContent);
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Error parsing Mistral API response: " + e.getMessage(), e));
        }
    }
}
