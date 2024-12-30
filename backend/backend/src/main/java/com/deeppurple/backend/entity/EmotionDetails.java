package com.deeppurple.backend.entity;

import jakarta.persistence.Embeddable;



@Embeddable
public class EmotionDetails {
    private String emotion;
    private double percentage;

    // Default constructor
    public EmotionDetails() {}

    // Parameterized constructor
    public EmotionDetails(String emotion, double percentage) {
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

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
