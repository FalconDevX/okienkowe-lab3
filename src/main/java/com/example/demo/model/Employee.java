package com.example.demo.model;

import jakarta.persistence.*;
import javafx.beans.property.*;

@Entity
@Table(name = "employees")
public class Employee extends AuditableEntity implements Comparable<Employee> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Convert(converter = EmployeeConditionConverter.class)
    @Column(name = "`condition`", nullable = false)
    private EmployeeCondition condition;

    @Column(name = "birth_year", nullable = false)
    private Integer birthYear;

    @Column(name = "salary", nullable = false)
    private Double salary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private ClassEmployee group;

    @Column(name = "deleted")
    private Boolean deleted = false;

    // JavaFX Properties (transient - nie zapisywane w bazie)
    @Transient
    private StringProperty firstNameProperty;
    @Transient
    private StringProperty lastNameProperty;
    @Transient
    private ObjectProperty<EmployeeCondition> conditionProperty;
    @Transient
    private IntegerProperty birthYearProperty;
    @Transient
    private DoubleProperty salaryProperty;

    public Employee() {
        initializeProperties();
    }

    public Employee(String firstName, String lastName, EmployeeCondition condition, int birthYear, double salary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.condition = condition;
        this.birthYear = birthYear;
        this.salary = salary;
        initializeProperties();
    }

    private void initializeProperties() {
        this.firstNameProperty = new SimpleStringProperty(firstName);
        this.lastNameProperty = new SimpleStringProperty(lastName);
        this.conditionProperty = new SimpleObjectProperty<>(condition);
        this.birthYearProperty = new SimpleIntegerProperty(birthYear != null ? birthYear : 0);
        this.salaryProperty = new SimpleDoubleProperty(salary != null ? salary : 0.0);
        
        // Synchronizacja properties z polami
        bindProperties();
    }

    private void bindProperties() {
        if (firstNameProperty != null) {
            firstNameProperty.addListener((obs, oldVal, newVal) -> this.firstName = newVal);
        }
        if (lastNameProperty != null) {
            lastNameProperty.addListener((obs, oldVal, newVal) -> this.lastName = newVal);
        }
        if (conditionProperty != null) {
            conditionProperty.addListener((obs, oldVal, newVal) -> this.condition = newVal);
        }
        if (birthYearProperty != null) {
            birthYearProperty.addListener((obs, oldVal, newVal) -> this.birthYear = newVal.intValue());
        }
        if (salaryProperty != null) {
            salaryProperty.addListener((obs, oldVal, newVal) -> this.salary = newVal.doubleValue());
        }
    }

    @PostLoad
    private void syncPropertiesAfterLoad() {
        if (firstNameProperty == null) {
            initializeProperties();
        }
        firstNameProperty.set(firstName);
        lastNameProperty.set(lastName);
        conditionProperty.set(condition);
        birthYearProperty.set(birthYear);
        salaryProperty.set(salary);
    }

    // Getters and Setters for database fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        if (firstNameProperty != null) {
            firstNameProperty.set(firstName);
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        if (lastNameProperty != null) {
            lastNameProperty.set(lastName);
        }
    }

    public EmployeeCondition getCondition() {
        return condition;
    }

    public void setCondition(EmployeeCondition condition) {
        this.condition = condition;
        if (conditionProperty != null) {
            conditionProperty.set(condition);
        }
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
        if (birthYearProperty != null) {
            birthYearProperty.set(birthYear);
        }
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
        if (salaryProperty != null) {
            salaryProperty.set(salary);
        }
    }

    public ClassEmployee getGroup() {
        return group;
    }

    public void setGroup(ClassEmployee group) {
        this.group = group;
    }

    // JavaFX Property getters (for compatibility with existing code)
    public StringProperty firstNameProperty() {
        if (firstNameProperty == null) {
            initializeProperties();
        }
        return firstNameProperty;
    }

    public StringProperty lastNameProperty() {
        if (lastNameProperty == null) {
            initializeProperties();
        }
        return lastNameProperty;
    }

    public ObjectProperty<EmployeeCondition> conditionProperty() {
        if (conditionProperty == null) {
            initializeProperties();
        }
        return conditionProperty;
    }

    public IntegerProperty birthYearProperty() {
        if (birthYearProperty == null) {
            initializeProperties();
        }
        return birthYearProperty;
    }

    public DoubleProperty salaryProperty() {
        if (salaryProperty == null) {
            initializeProperties();
        }
        return salaryProperty;
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

    // Soft delete
    public void delete() {
        this.deleted = true;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    // Restore dla usuniętych pracowników
    public void restore() {
        this.deleted = false;
    }
}

