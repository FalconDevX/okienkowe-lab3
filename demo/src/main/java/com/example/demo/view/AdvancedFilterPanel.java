package com.example.demo.view;

import com.example.demo.model.Employee;
import com.example.demo.model.EmployeeCondition;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.Year;

public class AdvancedFilterPanel extends VBox {
    private TextField lastNameFilter;
    private TextField minSalaryFilter;
    private TextField maxSalaryFilter;
    private CheckBox obecnyCheck;
    private CheckBox delegacjaCheck;
    private CheckBox choryCheck;
    private CheckBox nieobecnyCheck;
    private TextField minAgeFilter;
    private TextField maxAgeFilter;
    private Label resultCountLabel;
    private Button clearButton;
    private FilteredList<Employee> filteredList;

    public AdvancedFilterPanel() {
        super(10);
        this.setPadding(new Insets(10));
        this.setPrefWidth(250);
        this.getStyleClass().add("filter-panel");
        setupComponents();
    }

    private void setupComponents() {
        // Filtry nazwiska
        Label lastNameLabel = new Label("Nazwisko (fragment):");
        lastNameFilter = new TextField();
        lastNameFilter.setPromptText("Wpisz fragment nazwiska");

        // Filtry wynagrodzenia
        Label salaryLabel = new Label("Wynagrodzenie:");
        minSalaryFilter = new TextField();
        minSalaryFilter.setPromptText("Min");
        maxSalaryFilter = new TextField();
        maxSalaryFilter.setPromptText("Max");

        // CheckBoxy dla stanów
        Label conditionLabel = new Label("Stany:");
        obecnyCheck = new CheckBox("Obecny");
        obecnyCheck.setSelected(true);
        delegacjaCheck = new CheckBox("Delegacja");
        delegacjaCheck.setSelected(true);
        choryCheck = new CheckBox("Chory");
        choryCheck.setSelected(true);
        nieobecnyCheck = new CheckBox("Nieobecny");
        nieobecnyCheck.setSelected(true);

        // Filtry wieku
        Label ageLabel = new Label("Wiek:");
        minAgeFilter = new TextField();
        minAgeFilter.setPromptText("Min wiek");
        maxAgeFilter = new TextField();
        maxAgeFilter.setPromptText("Max wiek");

        // Licznik wyników
        resultCountLabel = new Label("Wyników: 0");

        // Przycisk wyczyść
        clearButton = new Button("Wyczyść filtry");
        clearButton.setOnAction(e -> clearFilters());

        // Dodanie komponentów
        this.getChildren().addAll(
                lastNameLabel, lastNameFilter,
                salaryLabel, minSalaryFilter, maxSalaryFilter,
                conditionLabel, obecnyCheck, delegacjaCheck, choryCheck, nieobecnyCheck,
                ageLabel, minAgeFilter, maxAgeFilter,
                resultCountLabel,
                clearButton
        );

        // Listeners do filtrowania na żywo
        setupListeners();
    }

    public void setFilteredList(FilteredList<Employee> filteredList) {
        this.filteredList = filteredList;
        applyFilters();
        
        // Aktualizacja licznika przy zmianie filtrowanej listy
        filteredList.addListener((javafx.collections.ListChangeListener.Change<? extends Employee> c) -> {
            updateResultCount();
        });
    }

    private void setupListeners() {
        lastNameFilter.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        minSalaryFilter.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        maxSalaryFilter.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        minAgeFilter.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        maxAgeFilter.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        
        obecnyCheck.selectedProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        delegacjaCheck.selectedProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        choryCheck.selectedProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        nieobecnyCheck.selectedProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void applyFilters() {
        if (filteredList == null) return;

        filteredList.setPredicate(employee -> {
            // Filtr nazwiska (case-insensitive)
            String lastNameText = lastNameFilter.getText().trim().toLowerCase();
            if (!lastNameText.isEmpty()) {
                if (!employee.getLastName().toLowerCase().contains(lastNameText)) {
                    return false;
                }
            }

            // Filtr wynagrodzenia
            try {
                if (!minSalaryFilter.getText().trim().isEmpty()) {
                    double minSalary = Double.parseDouble(minSalaryFilter.getText().trim());
                    if (employee.getSalary() < minSalary) {
                        return false;
                    }
                }
                if (!maxSalaryFilter.getText().trim().isEmpty()) {
                    double maxSalary = Double.parseDouble(maxSalaryFilter.getText().trim());
                    if (employee.getSalary() > maxSalary) {
                        return false;
                    }
                }
            } catch (NumberFormatException e) {
                // Ignoruj błędne wartości
            }

            // Filtr stanów
            EmployeeCondition condition = employee.getCondition();
            boolean conditionAllowed = switch (condition) {
                case OBECNY -> obecnyCheck.isSelected();
                case DELEGACJA -> delegacjaCheck.isSelected();
                case CHORY -> choryCheck.isSelected();
                case NIEOBECNY -> nieobecnyCheck.isSelected();
            };
            if (!conditionAllowed) {
                return false;
            }

            // Filtr wieku
            int currentYear = Year.now().getValue();
            int age = currentYear - employee.getBirthYear();
            try {
                if (!minAgeFilter.getText().trim().isEmpty()) {
                    int minAge = Integer.parseInt(minAgeFilter.getText().trim());
                    if (age < minAge) {
                        return false;
                    }
                }
                if (!maxAgeFilter.getText().trim().isEmpty()) {
                    int maxAge = Integer.parseInt(maxAgeFilter.getText().trim());
                    if (age > maxAge) {
                        return false;
                    }
                }
            } catch (NumberFormatException e) {
                // Ignoruj błędne wartości
            }

            return true;
        });

        updateResultCount();
    }

    private void updateResultCount() {
        if (filteredList != null) {
            resultCountLabel.setText("Wyników: " + filteredList.size());
        }
    }

    public void clearFilters() {
        lastNameFilter.clear();
        minSalaryFilter.clear();
        maxSalaryFilter.clear();
        minAgeFilter.clear();
        maxAgeFilter.clear();
        obecnyCheck.setSelected(true);
        delegacjaCheck.setSelected(true);
        choryCheck.setSelected(true);
        nieobecnyCheck.setSelected(true);
    }
}

