package com.example.demo.service;

/**
 * Klasa pomocnicza do przechowywania statystyk pracowników
 */
public class EmployeeStatistics {
    private long count;
    private double averageSalary;
    private double minSalary;
    private double maxSalary;

    public EmployeeStatistics(long count, Double averageSalary, Double minSalary, Double maxSalary) {
        this.count = count;
        this.averageSalary = averageSalary != null ? averageSalary : 0.0;
        this.minSalary = minSalary != null ? minSalary : 0.0;
        this.maxSalary = maxSalary != null ? maxSalary : 0.0;
    }

    public long getCount() {
        return count;
    }

    public double getAverageSalary() {
        return averageSalary;
    }

    public double getMinSalary() {
        return minSalary;
    }

    public double getMaxSalary() {
        return maxSalary;
    }

    @Override
    public String toString() {
        return String.format("EmployeeStatistics{count=%d, avgSalary=%.2f, minSalary=%.2f, maxSalary=%.2f}",
                count, averageSalary, minSalary, maxSalary);
    }
}

