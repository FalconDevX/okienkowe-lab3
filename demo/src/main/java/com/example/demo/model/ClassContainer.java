package com.example.demo.model;

import java.util.*;
import java.util.stream.Collectors;
//ClassContainer jako TreeMap

public class ClassContainer {

    // Pola
    private Map<String, ClassEmployee> groups;
    private StorageMode currentMode;

    // Konstruktory
    // konstruktor bez parametrów
    public ClassContainer() {
        this.groups = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    // konstruktor z comparator
    public ClassContainer(Comparator<String> comparator) {
        this.groups = new TreeMap<>(comparator);
    }

    // konstruktor z StorageMode
    public ClassContainer(StorageMode mode) {
        this.currentMode = mode;
        switch (mode) {
            case HASH_MAP -> groups = new HashMap<>();
            case LINKED_HASH_MAP -> groups = new LinkedHashMap<>();
            case TREE_MAP -> groups = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        }
    }

    // Metody
    // dodawanie klasy
    public void addClass(String name, int capacity) {
        groups.put(name, new ClassEmployee(name, capacity));
    }

    // usuwanie klasy
    public void removeClass(String name) {
        groups.remove(name);
    }

    // pobieranie listy klas w porządku alfabetycznym
    public List<String> getGroupsInOrder() {
        return new ArrayList<>(groups.keySet());
    }

    // pobieranie klasy po nazwie
    public ClassEmployee getGroup(String name) {
        return groups.get(name);
    }

    // pobieranie listy pustych klas
    public List<String> findEmpty() {
        return groups.entrySet().stream()
                .filter(e -> e.getValue().countByCondition(EmployeeCondition.OBECNY)
                        + e.getValue().countByCondition(EmployeeCondition.DELEGACJA)
                        + e.getValue().countByCondition(EmployeeCondition.NIEOBECNY)
                        + e.getValue().countByCondition(EmployeeCondition.CHORY) == 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // podsumowanie
    public void summary() {
        groups.forEach((k, v) -> {
            int total = v.sortByName().size();
            System.out.println(k + " — " + total + " pracowników");
        });
    }

    // liczenie pracowników w klasach
    public Map<String, Integer> countEmployeesInGroups() {
        return groups.entrySet().stream()
                .filter(e -> !e.getValue().sortByName().isEmpty())
                .sorted((a, b) -> Integer.compare(
                        b.getValue().sortByName().size(),
                        a.getValue().sortByName().size()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().sortByName().size(),
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    //zad7
    //klasa jako HashMap, LinkedHashMap, TreeMap
    public void changeStorageMode(StorageMode newMode) {
        Map<String, ClassEmployee> newMap;

        switch (newMode) {
            case HASH_MAP -> newMap = new HashMap<>();
            case LINKED_HASH_MAP -> newMap = new LinkedHashMap<>();
            case TREE_MAP -> newMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            default -> throw new IllegalStateException();
        }

        newMap.putAll(groups);
        groups = newMap;
        currentMode = newMode;
    }

    public void demonstrateOrderDifferences() {
        System.out.println("Aktualny tryb: " + currentMode);
        System.out.println("Kolejność grup: " + groups.keySet());
    }

}

