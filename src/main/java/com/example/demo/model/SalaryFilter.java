package com.example.demo.model;

import java.util.function.Predicate;

public class SalaryFilter {
    public static Predicate<Employee> minSalary(double min) {
        return e -> e.getSalary() >= min;
    }

    public static Predicate<Employee> salaryRange(double min, double max) {
        return e -> e.getSalary() >= min && e.getSalary() <= max;
    }
}

