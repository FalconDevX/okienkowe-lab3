package com.example.demo.model;

import java.util.List;

public class ConditionAnalyzer {

    public static boolean hasCondition(List<Employee> employees, EmployeeCondition cond) {
        return employees.stream().anyMatch(e -> e.getCondition() == cond);
    }

    public static double getConditionPercentage(List<Employee> employees, EmployeeCondition cond) {
        if (employees.isEmpty()) return 0.0;

        long count = employees.stream()
                .filter(e -> e.getCondition() == cond)
                .count();

        return (count * 100.0) / employees.size();
    }
}

