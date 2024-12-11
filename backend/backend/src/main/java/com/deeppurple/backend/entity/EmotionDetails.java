package com.deeppurple.backend.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class EmotionDetails {
    private String emotion;
    private int percentage;

    // Default constructor
    public EmotionDetails() {}

    // Parameterized constructor
    public EmotionDetails(String emotion, int percentage) {
        this.emotion = emotion;
        this.percentage = percentage;
    }

    // Getters and Setters
    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
