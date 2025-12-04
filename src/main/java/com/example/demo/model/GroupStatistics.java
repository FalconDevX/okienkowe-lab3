package com.example.demo.model;

import javafx.beans.property.*;

/**
 * Klasa pomocnicza do wyświetlania statystyk grupy w tabeli
 */
public class GroupStatistics {
    private final StringProperty groupName;
    private final LongProperty ratingCount;
    private final DoubleProperty averageRating;

    public GroupStatistics(String groupName, Long ratingCount, Double averageRating) {
        this.groupName = new SimpleStringProperty(groupName);
        this.ratingCount = new SimpleLongProperty(ratingCount != null ? ratingCount : 0);
        this.averageRating = new SimpleDoubleProperty(averageRating != null ? averageRating : 0.0);
    }

    public StringProperty groupNameProperty() {
        return groupName;
    }

    public String getGroupName() {
        return groupName.get();
    }

    public void setGroupName(String groupName) {
        this.groupName.set(groupName);
    }

    public LongProperty ratingCountProperty() {
        return ratingCount;
    }

    public Long getRatingCount() {
        return ratingCount.get();
    }

    public void setRatingCount(Long ratingCount) {
        this.ratingCount.set(ratingCount);
    }

    public DoubleProperty averageRatingProperty() {
        return averageRating;
    }

    public Double getAverageRating() {
        return averageRating.get();
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating.set(averageRating);
    }
}

