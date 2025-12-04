package com.example.demo.service;

import com.example.demo.model.EmployeeCondition;

/**
 * DTO do dynamicznego filtrowania pracowników
 */
public class EmployeeFilterDTO {
    private String lastName;
    private Double minSalary;
    private Double maxSalary;
    private EmployeeCondition condition;
    private Integer birthYearFrom;
    private Integer birthYearTo;
    private String groupName;
    private String sortBy = "lastName";
    private String sortDirection = "ASC";
    private int page = 1;
    private int pageSize = 20;

    // Getters and Setters
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Double getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(Double minSalary) {
        this.minSalary = minSalary;
    }

    public Double getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(Double maxSalary) {
        this.maxSalary = maxSalary;
    }

    public EmployeeCondition getCondition() {
        return condition;
    }

    public void setCondition(EmployeeCondition condition) {
        this.condition = condition;
    }

    public Integer getBirthYearFrom() {
        return birthYearFrom;
    }

    public void setBirthYearFrom(Integer birthYearFrom) {
        this.birthYearFrom = birthYearFrom;
    }

    public Integer getBirthYearTo() {
        return birthYearTo;
    }

    public void setBirthYearTo(Integer birthYearTo) {
        this.birthYearTo = birthYearTo;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    // Builder pattern
    public static class Builder {
        private final EmployeeFilterDTO dto = new EmployeeFilterDTO();

        public Builder lastName(String lastName) {
            dto.setLastName(lastName);
            return this;
        }

        public Builder minSalary(Double minSalary) {
            dto.setMinSalary(minSalary);
            return this;
        }

        public Builder maxSalary(Double maxSalary) {
            dto.setMaxSalary(maxSalary);
            return this;
        }

        public Builder condition(EmployeeCondition condition) {
            dto.setCondition(condition);
            return this;
        }

        public Builder birthYearFrom(Integer birthYearFrom) {
            dto.setBirthYearFrom(birthYearFrom);
            return this;
        }

        public Builder birthYearTo(Integer birthYearTo) {
            dto.setBirthYearTo(birthYearTo);
            return this;
        }

        public Builder groupName(String groupName) {
            dto.setGroupName(groupName);
            return this;
        }

        public Builder sortBy(String sortBy) {
            dto.setSortBy(sortBy);
            return this;
        }

        public Builder sortDirection(String sortDirection) {
            dto.setSortDirection(sortDirection);
            return this;
        }

        public Builder page(int page) {
            dto.setPage(page);
            return this;
        }

        public Builder pageSize(int pageSize) {
            dto.setPageSize(pageSize);
            return this;
        }

        public EmployeeFilterDTO build() {
            return dto;
        }
    }
}

