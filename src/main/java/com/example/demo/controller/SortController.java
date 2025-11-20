package com.example.demo.controller;

import com.example.demo.model.Employee;
import com.example.demo.model.EmployeeCondition;
import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Duration;

import java.time.Year;
import java.util.Comparator;

public class SortController {
    public enum SortOrder {
        ASCENDING, DESCENDING
    }

    private SortOrder lastNameOrder = SortOrder.ASCENDING;
    private SortOrder salaryOrder = SortOrder.ASCENDING;
    private SortOrder ageOrder = SortOrder.ASCENDING;
    private SortOrder conditionOrder = SortOrder.ASCENDING;

    public void sortByLastName(ObservableList<Employee> employees, TableView<Employee> tableView) {
        Comparator<Employee> comparator = Comparator.comparing(Employee::getLastName);
        if (lastNameOrder == SortOrder.DESCENDING) {
            comparator = comparator.reversed();
            lastNameOrder = SortOrder.ASCENDING;
        } else {
            lastNameOrder = SortOrder.DESCENDING;
        }
        employees.sort(comparator);
        animateSort(tableView);
        updateColumnHeader(tableView, 1, lastNameOrder == SortOrder.ASCENDING ? "↓" : "↑");
    }

    public void sortBySalary(ObservableList<Employee> employees, TableView<Employee> tableView) {
        Comparator<Employee> comparator = Comparator.comparing(Employee::getSalary);
        if (salaryOrder == SortOrder.DESCENDING) {
            comparator = comparator.reversed();
            salaryOrder = SortOrder.ASCENDING;
        } else {
            salaryOrder = SortOrder.DESCENDING;
        }
        employees.sort(comparator);
        animateSort(tableView);
        updateColumnHeader(tableView, 4, salaryOrder == SortOrder.ASCENDING ? "↓" : "↑");
    }

    public void sortByAge(ObservableList<Employee> employees, TableView<Employee> tableView) {
        int currentYear = Year.now().getValue();
        Comparator<Employee> comparator = Comparator.comparing(e -> currentYear - e.getBirthYear());
        if (ageOrder == SortOrder.DESCENDING) {
            comparator = comparator.reversed();
            ageOrder = SortOrder.ASCENDING;
        } else {
            ageOrder = SortOrder.DESCENDING;
        }
        employees.sort(comparator);
        animateSort(tableView);
        updateColumnHeader(tableView, 3, ageOrder == SortOrder.ASCENDING ? "↓" : "↑");
    }

    public void sortByCondition(ObservableList<Employee> employees, TableView<Employee> tableView) {
        Comparator<Employee> comparator = Comparator.comparing(e -> {
            EmployeeCondition cond = e.getCondition();
            int priority = switch (cond) {
                case OBECNY -> 1;
                case DELEGACJA -> 2;
                case CHORY -> 3;
                case NIEOBECNY -> 4;
            };
            return priority;
        });
        if (conditionOrder == SortOrder.DESCENDING) {
            comparator = comparator.reversed();
            conditionOrder = SortOrder.ASCENDING;
        } else {
            conditionOrder = SortOrder.DESCENDING;
        }
        employees.sort(comparator);
        animateSort(tableView);
        updateColumnHeader(tableView, 2, conditionOrder == SortOrder.ASCENDING ? "↓" : "↑");
    }

    public void sortMultiCriteria(ObservableList<Employee> employees, TableView<Employee> tableView, 
                                  boolean byCondition, boolean byLastName) {
        Comparator<Employee> comparator = Comparator.comparing(e -> {
            if (byCondition) {
                EmployeeCondition cond = e.getCondition();
                return switch (cond) {
                    case OBECNY -> 1;
                    case DELEGACJA -> 2;
                    case CHORY -> 3;
                    case NIEOBECNY -> 4;
                };
            }
            return 0;
        });
        
        if (byLastName) {
            comparator = comparator.thenComparing(Employee::getLastName);
        }
        
        employees.sort(comparator);
        animateSort(tableView);
    }

    private void animateSort(TableView<Employee> tableView) {
        FadeTransition fade = new FadeTransition(Duration.millis(200), tableView);
        fade.setFromValue(1.0);
        fade.setToValue(0.5);
        fade.setAutoReverse(true);
        fade.setCycleCount(2);
        fade.play();
    }

    private void updateColumnHeader(TableView<Employee> tableView, int columnIndex, String indicator) {
        TableColumn<Employee, ?> column = tableView.getColumns().get(columnIndex);
        String originalText = column.getText();
        // Usuń poprzednie wskaźniki
        originalText = originalText.replaceAll(" [↑↓]", "");
        column.setText(originalText + " " + indicator);
    }

    public void clearIndicators(TableView<Employee> tableView) {
        for (TableColumn<Employee, ?> column : tableView.getColumns()) {
            String text = column.getText();
            text = text.replaceAll(" [↑↓]", "");
            column.setText(text);
        }
    }
}

