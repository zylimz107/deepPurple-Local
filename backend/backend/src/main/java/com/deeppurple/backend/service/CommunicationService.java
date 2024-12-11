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

    public CommunicationService(CommunicationRepository repository, OpenAIService openAIService) {
        this.repository = repository;
        this.openAIService = openAIService;
    }

    // Retrieve all communications
    public List<Communication> getAllCommunications() {
        return repository.findAll();
    }

    // Save new communication with model and classification type
    public Mono<Communication> saveCommunication(String modelName, Communication communication) {
        return openAIService.analyzeEmotionWithModel(communication.getContent(), modelName)
                .map(emotionAnalysis -> {
                    // Extract values from the emotion analysis
                    Map<String, Object> primaryEmotionData = (Map<String, Object>) emotionAnalysis.get("primaryEmotion");
                    String primaryEmotion = (String) primaryEmotionData.get("emotion");
                    int primaryEmotionPercentage = (int) primaryEmotionData.get("percentage");

                    List<Map<String, Object>> secondaryEmotionsData = (List<Map<String, Object>>) emotionAnalysis.get("secondaryEmotions");
                    List<EmotionDetails> secondaryEmotions = secondaryEmotionsData.stream()
                            .map(emotion -> new EmotionDetails((String) emotion.get("emotion"), (int) emotion.get("percentage")))
                            .collect(Collectors.toList());

                    String summary = (String) emotionAnalysis.get("summary");
                    int confidenceRating = (int) emotionAnalysis.get("confidenceRating");

                    // Set the fields in the communication object
                    communication.setPrimaryEmotion(new EmotionDetails(primaryEmotion, primaryEmotionPercentage));
                    communication.setSecondaryEmotions(secondaryEmotions);
                    communication.setSummary(summary);
                    communication.setConfidenceRating(confidenceRating);
                    System.out.println("Primary Emotion: " + communication.getPrimaryEmotion());
                    System.out.println("Secondary Emotions: " + communication.getSecondaryEmotions());


                    return repository.save(communication);
                });
    }

    // Get communication by ID
    public Mono<Communication> getCommunicationById(Long id) {
        return Mono.justOrEmpty(repository.findById(id)); // Return Mono.empty() if not found
    }

    // Update communication by ID with re-analysis
    public Mono<Communication> updateCommunication(Long id, String modelName, Communication updatedCommunication) {
        return getCommunicationById(id)
                .flatMap(existingCommunication -> {
                    existingCommunication.setContent(updatedCommunication.getContent());

                    // Re-analyze the emotion for the updated content
                    return openAIService.analyzeEmotionWithModel(existingCommunication.getContent(), modelName)
                            .map(emotionAnalysis -> {
                                Map<String, Object> primaryEmotionData = (Map<String, Object>) emotionAnalysis.get("primaryEmotion");
                                String primaryEmotion = (String) primaryEmotionData.get("emotion");
                                int primaryEmotionPercentage = (int) primaryEmotionData.get("percentage");

                                List<Map<String, Object>> secondaryEmotionsData = (List<Map<String, Object>>) emotionAnalysis.get("secondaryEmotions");
                                List<EmotionDetails> secondaryEmotions = secondaryEmotionsData.stream()
                                        .map(emotion -> new EmotionDetails((String) emotion.get("emotion"), (int) emotion.get("percentage")))
                                        .collect(Collectors.toList());

                                String summary = (String) emotionAnalysis.get("summary");

                                existingCommunication.setPrimaryEmotion(new EmotionDetails(primaryEmotion, primaryEmotionPercentage));
                                existingCommunication.setSecondaryEmotions(secondaryEmotions);
                                existingCommunication.setSummary(summary);

                                return repository.save(existingCommunication);
                            });
                });
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
