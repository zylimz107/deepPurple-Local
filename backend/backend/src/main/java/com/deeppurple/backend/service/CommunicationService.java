package com.deeppurple.backend.service;

import com.deeppurple.backend.entity.Communication;
import com.deeppurple.backend.entity.EmotionDetails;
import com.deeppurple.backend.repository.CommunicationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommunicationService {
    private final CommunicationRepository repository;
    private final OpenAIService openAIService;
    private final MissingEmotionService missingEmotionService;

    public CommunicationService(CommunicationRepository repository, OpenAIService openAIService, MissingEmotionService missingEmotionService) {
        this.repository = repository;
        this.openAIService = openAIService;
        this.missingEmotionService = missingEmotionService;
    }

    // Retrieve all communications
    public List<Communication> getAllCommunications() {
        return repository.findAll();
    }

    // Save new communication with model and classification type
    public Mono<Communication> saveCommunication(String modelName, Communication communication) {
        // Step 1: Ensure missing emotions are identified and stored first
        return missingEmotionService.processMissingEmotions(communication.getContent(), modelName)
                .then(Mono.defer(() -> { // Step 2: Call OpenAI analysis only after step 1 is completed
                    return openAIService.analyzeEmotionWithModel(communication.getContent(), modelName)
                            .map(emotionAnalysis -> {
                                // Extract values from the emotion analysis
                                Map<String, Object> primaryEmotionData = (Map<String, Object>) emotionAnalysis.get("primaryEmotion");
                                String primaryEmotion = (String) primaryEmotionData.get("emotion");
                                double primaryEmotionPercentage = (double) primaryEmotionData.get("percentage");

                                List<Map<String, Object>> secondaryEmotionsData = (List<Map<String, Object>>) emotionAnalysis.get("secondaryEmotions");
                                List<EmotionDetails> secondaryEmotions = secondaryEmotionsData.stream()
                                        .map(emotion -> new EmotionDetails((String) emotion.get("emotion"), (double) emotion.get("percentage")))
                                        .collect(Collectors.toList());

                                String summary = (String) emotionAnalysis.get("summary");
                                String modelVersion = (String) emotionAnalysis.get("modelVersion");
                                double confidenceRating = (double) emotionAnalysis.get("confidenceRating");

                                // Set the fields in the communication object
                                communication.setPrimaryEmotion(new EmotionDetails(primaryEmotion, primaryEmotionPercentage));
                                communication.setSecondaryEmotions(secondaryEmotions);
                                communication.setSummary(summary);
                                communication.setConfidenceRating(confidenceRating);
                                communication.setModelVersion(modelVersion);
                                System.out.println("Primary Emotion: " + communication.getPrimaryEmotion());
                                System.out.println("Secondary Emotions: " + communication.getSecondaryEmotions());

                                return repository.save(communication);
                            });
                }));
    }


    // Get communication by ID
    public Mono<Communication> getCommunicationById(Long id) {
        return Mono.justOrEmpty(repository.findById(id)); // Return Mono.empty() if not found
    }

    // Delete communication by ID
    public Mono<Boolean> deleteCommunication(Long id) {
        return getCommunicationById(id)
                .flatMap(existingCommunication -> {
                    if (existingCommunication != null) {
                        repository.deleteById(id);
                        return Mono.just(true); // Successfully deleted
                    }
                    return Mono.just(false); // Communication not found
                });
    }
}
