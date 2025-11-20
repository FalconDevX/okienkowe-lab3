package com.example.demo.model;

import javafx.beans.property.*;

public class Employee implements Comparable<Employee> {
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final ObjectProperty<EmployeeCondition> condition;
    private final IntegerProperty birthYear;
    private final DoubleProperty salary;

    public Employee(String firstName, String lastName, EmployeeCondition condition, int birthYear, double salary) {
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.condition = new SimpleObjectProperty<>(condition);
        this.birthYear = new SimpleIntegerProperty(birthYear);
        this.salary = new SimpleDoubleProperty(salary);
    }

    // Property getters
    public StringProperty firstNameProperty() {
        return firstName;
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public ObjectProperty<EmployeeCondition> conditionProperty() {
        return condition;
    }

    public IntegerProperty birthYearProperty() {
        return birthYear;
    }

    public DoubleProperty salaryProperty() {
        return salary;
    }

    // Value getters
    public String getFirstName() {
        return firstName.get();
    }

    public String getLastName() {
        return lastName.get();
    }

    public EmployeeCondition getCondition() {
        return condition.get();
    }

    public int getBirthYear() {
        return birthYear.get();
    }

    public double getSalary() {
        return salary.get();
    }

    // Value setters
    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public void setCondition(EmployeeCondition condition) {
        this.condition.set(condition);
    }

    public void setBirthYear(int birthYear) {
        this.birthYear.set(birthYear);
    }

    public void setSalary(double salary) {
        this.salary.set(salary);
    }

    // printing
    public void printing() {
        System.out.println(getFirstName() + " " + getLastName() + " " + getCondition() + " " + getBirthYear() + " " + getSalary());
    }

    // compare to - sortowanie po nazwisku
    @Override
    public int compareTo(Employee other) {
        return this.getLastName().compareTo(other.getLastName());
    }

    // usuwanie duplikatów
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Employee))
            return false;

        Employee e = (Employee) obj;
        return this.getFirstName().equals(e.getFirstName()) && this.getLastName().equals(e.getLastName());
    }

    // hashowanie w celu usuwania duplikatów
    @Override
    public int hashCode() {
        return java.util.Objects.hash(getFirstName(), getLastName());
    }
}

