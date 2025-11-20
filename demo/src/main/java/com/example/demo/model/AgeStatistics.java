package com.example.demo.model;

public class AgeStatistics {
    private int youngest;
    private int oldest;
    private double average;
    private int count;

    public AgeStatistics(int youngest, int oldest, double average, int count) {
        this.youngest = youngest;
        this.oldest = oldest;
        this.average = average;
        this.count = count;
    }

    @Override
    public String toString() {
        return "wiek najmł.: " + youngest +
               ", wiek najst.: " + oldest +
               ", średni: " + average +
               ", liczba: " + count;
    }
}

