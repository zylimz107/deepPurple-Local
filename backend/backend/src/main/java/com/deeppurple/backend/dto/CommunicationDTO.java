package com.deeppurple.backend.dto;

import com.deeppurple.backend.entity.EmotionDetails;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public class CommunicationDTO {

    @NotBlank(message = "Content cannot be empty")
    @Size(max = 1000, message = "Content cannot exceed 1000 characters")
    private String content;

    @NotBlank(message = "Model name cannot be empty")
    private String modelName; // Added to specify the model used for analysis

    private EmotionDetails primaryEmotion; // Updated to capture the primary emotion with percentage
    private List<EmotionDetails> secondaryEmotions; // Updated to capture secondary emotions with percentages
    private String summary; // Optional: to store the analysis summary
    private double confidenceRating;
    private String modelVersion;


    // Default constructor
    public CommunicationDTO(double confidenceRating) {
        this.confidenceRating = confidenceRating;
    }

    // Parameterized constructor
    public CommunicationDTO(String content, String modelName, double confidenceRating, String modelVersion) {
        this.content = content;
        this.modelName = modelName;
        this.confidenceRating = confidenceRating;
        this.modelVersion = modelVersion;
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public EmotionDetails getPrimaryEmotion() {
        return primaryEmotion;
    }

    public void setPrimaryEmotion(EmotionDetails primaryEmotion) {
        this.primaryEmotion = primaryEmotion;
    }

    public List<EmotionDetails> getSecondaryEmotions() {
        return secondaryEmotions;
    }

    public void setSecondaryEmotions(List<EmotionDetails> secondaryEmotions) {
        this.secondaryEmotions = secondaryEmotions;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }


    public double getConfidenceRating() {
        return confidenceRating;
    }

    public void setConfidenceRating(double confidenceRating) {
        this.confidenceRating = confidenceRating;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }
}
