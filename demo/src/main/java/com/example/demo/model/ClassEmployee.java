package com.example.demo.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.EnumMap;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.Optional;
import java.time.Year;
import java.util.IntSummaryStatistics;

public class ClassEmployee {
    private String groupName;
    private List<Employee> employees;
    private int maxCapacity;

    // konstruktor
    public ClassEmployee(String groupName, int maxCapacity) {
        this.groupName = groupName;
        this.employees = new ArrayList<>();
        this.maxCapacity = maxCapacity;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    // check if employee exists
    public boolean checkEmployee(Employee employee) {
        for (Employee e : employees) {
            if (e.getFirstName().equals(employee.getFirstName()) && e.getLastName().equals(employee.getLastName())) {
                return true;
            }
        }
        return false;
    }

    // add employee
    // jesli pracownik imieniu nazwisku istnieje lub brak mejsca to komunikat
    // inaczej dodaj
    public boolean addEmployee(Employee employee) {
        // Sprawdzenie czy pracownik o imieniu i nazwisku juz istnieje
        if (checkEmployee(employee)) {
            System.out.println("Pracownik o imieniu i nazwisku juz istnieje");
            return false;
        }
        // Sprawdzenie czy jest miejsce w grupie
        if (employees.size() >= maxCapacity) {
            System.out.println("Brak miejsca w grupie");
            return false;
        }
        employees.add(employee);
        return true;
    }

    // remove employee
    public boolean removeEmployee(Employee employee) {
        // Sprawdzenie czy pracownik o imieniu i nazwisku istnieje
        if (!checkEmployee(employee)) {
            System.out.println("Pracownik o imieniu i nazwisku nie istnieje");
            return false;
        }
        return employees.remove(employee);
    }

    // change condition
    public void changeCondition(Employee employee, EmployeeCondition condition) {
        if (!checkEmployee(employee)) {
            System.out.println("Pracownik o imieniu i nazwisku nie istnieje");
            return;
        }
        employee.setCondition(condition);
    }

    // add salary
    public void addSalary(Employee employee, double amount) {
        if (!checkEmployee(employee)) {
            System.out.println("Pracownik o imieniu i nazwisku nie istnieje");
            return;
        }
        employee.setSalary(employee.getSalary() + amount);
    }

    // wyszukiwanie po nazwisku przez comparator
    public Employee searchByLastName(String lastName) {
        return employees.stream()
                .filter(e -> e.getLastName().equals(lastName))
                .findFirst()
                .orElse(null);
    }

    // szukanie częsciowe
    public List<Employee> searchByPartial(String fragment) {
        return employees.stream()
                .filter(e -> e.getFirstName().contains(fragment) || e.getLastName().contains(fragment))
                .collect(Collectors.toList());
    }

    // liczenie po condition
    public long countByCondition(EmployeeCondition condition) {
        return employees.stream()
                .filter(e -> e.getCondition() == condition)
                .count();
    }

    // podsumowanie
    public void summary() {
        for (Employee e : employees) {
            e.printing();
        }
    }

    // sortowanie po imieniu z compareTo
    public List<Employee> sortByName() {
        return employees.stream().sorted().collect(Collectors.toList());
    }

    // sortowanie malejąco po pensji - własny comparator
    public List<Employee> sortbySalary() {
        return employees.stream().sorted((a, b) -> Double.compare(b.getSalary(), a.getSalary()))
                .collect(Collectors.toList());
    }

    // największy wg compareTo (po nazwisku)
    public Employee max() {
        return Collections.max(employees);
    }

    /////////////////////////////////////////////////////////////////////////////
    // usuwanie duplikatów
    public int removeDuplicates() {
        if (employees.isEmpty())
            return 0;

        Set<Employee> seen = new HashSet<>();
        List<Employee> newList = new ArrayList<>();
        int removed = 0;

        for (Employee e : employees) {
            if (seen.add(e)) {
                newList.add(e); // pierwsze wystąpienie
            } else {
                removed++; // duplikat
            }
        }

        employees = newList;

        return removed;
    }

    // grupowanie po condition
    public Map<EmployeeCondition, List<Employee>> groupByCondition() {
        Map<EmployeeCondition, List<Employee>> result = new EnumMap<>(EmployeeCondition.class);
        
        // Inicjalizacja wszystkich stanów pustymi listami
        for (EmployeeCondition c : EmployeeCondition.values()) {
            result.put(c, new ArrayList<>());
        }
        
        // Dodanie pracowników do odpowiednich stanów, posortowanych po nazwisku
        employees.stream()
                .sorted(Comparator.comparing(Employee::getLastName))
                .forEach(e -> result.get(e.getCondition()).add(e));
        
        return result;
    }

    // mediana salary
    public double medianSalary() {
        if (employees.isEmpty())
            return 0;

        List<Double> salaries = employees.stream()
                .map(Employee::getSalary)
                .sorted()
                .collect(Collectors.toList());

        int n = salaries.size();

        if (n % 2 == 1) {
            return salaries.get(n / 2);
        } else {
            return (salaries.get(n / 2 - 1) + salaries.get(n / 2)) / 2;
        }
    }

    // zad6 youngest, oldest employee
    public Optional<Employee> oldestEmployee() {
        return employees.stream()
                .max(Comparator.comparing(Employee::getBirthYear).reversed());
    }

    public Optional<Employee> youngestEmployee() {
        return employees.stream()
                .min(Comparator.comparing(Employee::getBirthYear));
    }

    public double getAverageAge() {
        int current = Year.now().getValue();
        return employees.stream()
                .mapToInt(e -> current - e.getBirthYear())
                .average()
                .orElse(0.0);
    }

    public AgeStatistics getAgeStatistics() {
        int current = Year.now().getValue();

        IntSummaryStatistics stats = employees.stream()
                .mapToInt(e -> current - e.getBirthYear())
                .summaryStatistics();

        return new AgeStatistics(
                stats.getMin(),
                stats.getMax(),
                stats.getAverage(),
                employees.size());
    }

    //zad7
    //filtorwanie po wynagrodzeniu
    public List<Employee> filterByMinSalary(double minSalary) {
        return employees.stream()
                .filter(SalaryFilter.minSalary(minSalary))
                .sorted((a, b) -> Double.compare(b.getSalary(), a.getSalary()))
                .collect(Collectors.toList());
    }
    
    public List<Employee> filterBySalaryRange(double min, double max) {
        return employees.stream()
                .filter(SalaryFilter.salaryRange(min, max))
                .sorted((a, b) -> Double.compare(b.getSalary(), a.getSalary()))
                .collect(Collectors.toList());
    }
    
    public List<Employee> getTopEarners(int count) {
        return employees.stream()
                .sorted((a, b) -> Double.compare(b.getSalary(), a.getSalary()))
                .limit(count)
                .collect(Collectors.toList());
    }
    
    public List<Employee> filterByPercentile(double percentile) {
        int index = (int) Math.ceil((percentile / 100.0) * employees.size()) - 1;
    
        return employees.stream()
                .sorted(Comparator.comparing(Employee::getSalary))
                .skip(index)
                .collect(Collectors.toList());
    }
    
    //zad10 grupowanie po wynagrodzeniu
    public Map<Double, List<Employee>> groupBySalary() {
        return employees.stream()
                .collect(Collectors.groupingBy(Employee::getSalary));
    }
    
    public Map<String, List<Employee>> groupBySalaryRange(double rangeSize) {
        return employees.stream()
                .collect(Collectors.groupingBy(e -> {
                    double floor = Math.floor(e.getSalary() / rangeSize) * rangeSize;
                    double ceil = floor + rangeSize;
                    return (int)floor + "-" + (int)ceil;
                }));
    }

}

