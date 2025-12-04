package com.example.demo.service;

/**
 * DTO do przechowywania statystyk grupy (używane w HQL)
 */
public class GroupStatisticsDTO {
    private String groupName;
    private long employeeCount;
    private double averageSalary;
    private long ratingCount;
    private double averageRating;
    private int maxCapacity;

    // Konstruktor używany przez HQL
    public GroupStatisticsDTO(String groupName, Long employeeCount,
                             Double averageSalary, Long ratingCount,
                             Double averageRating, Integer maxCapacity) {
        this.groupName = groupName;
        this.employeeCount = employeeCount != null ? employeeCount : 0;
        this.averageSalary = averageSalary != null ? averageSalary : 0.0;
        this.ratingCount = ratingCount != null ? ratingCount : 0;
        this.averageRating = averageRating != null ? averageRating : 0.0;
        this.maxCapacity = maxCapacity != null ? maxCapacity : 0;
    }

    public String getGroupName() {
        return groupName;
    }

    public long getEmployeeCount() {
        return employeeCount;
    }

    public double getAverageSalary() {
        return averageSalary;
    }

    public long getRatingCount() {
        return ratingCount;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public double getFillPercentage() {
        return maxCapacity == 0 ? 0 : (double) employeeCount / maxCapacity * 100;
    }
}

