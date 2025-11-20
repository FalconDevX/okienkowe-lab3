package com.example.demo.view;

import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import com.example.demo.model.Employee;

public class FilterPanel extends HBox {
    private TextField filterTextField;
    private FilteredList<Employee> filteredList;

    public FilterPanel() {
        super(10);
        setupFilter();
        this.getChildren().addAll(new Label("Filtruj po nazwisku:"), filterTextField);
        this.setStyle("-fx-padding: 10;");
    }

    private void setupFilter() {
        filterTextField = new TextField();
        filterTextField.setPrefWidth(300);
        filterTextField.setPromptText("Wpisz nazwisko i naciśnij ENTER");
    }

    public void setFilteredList(FilteredList<Employee> filteredList) {
        this.filteredList = filteredList;
        
        // Filtrowanie po naciśnięciu ENTER
        filterTextField.setOnAction(e -> applyFilter());
        
        // Filtrowanie podczas wpisywania (opcjonalnie)
        filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilter();
        });
    }

    private void applyFilter() {
        if (filteredList != null) {
            String filterText = filterTextField.getText().trim().toLowerCase();
            filteredList.setPredicate(employee -> {
                if (filterText.isEmpty()) {
                    return true;
                }
                return employee.getLastName().toLowerCase().contains(filterText);
            });
        }
    }

    public TextField getFilterTextField() {
        return filterTextField;
    }
}

