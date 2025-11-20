package com.example.demo.view;

import com.example.demo.model.Employee;
import com.example.demo.model.EmployeeCondition;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.util.Optional;

public class AddEmployeeDialog extends Dialog<Employee> {
    private TextField firstNameField;
    private TextField lastNameField;
    private TextField birthYearField;
    private TextField salaryField;
    private ComboBox<EmployeeCondition> conditionCombo;
    private Label errorLabel;
    private ButtonType addButtonType;

    public AddEmployeeDialog() {
        this(null);
    }

    public AddEmployeeDialog(Employee employee) {
        setTitle(employee == null ? "Dodaj pracownika" : "Edytuj pracownika");
        setHeaderText(employee == null ? "Dodawanie nowego pracownika" : "Edycja pracownika");

        addButtonType = new ButtonType(employee == null ? "Dodaj" : "Zapisz", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Imię
        grid.add(new Label("Imię:"), 0, 0);
        firstNameField = new TextField();
        if (employee != null) {
            firstNameField.setText(employee.getFirstName());
        }
        grid.add(firstNameField, 1, 0);

        // Nazwisko
        grid.add(new Label("Nazwisko:"), 0, 1);
        lastNameField = new TextField();
        if (employee != null) {
            lastNameField.setText(employee.getLastName());
        }
        grid.add(lastNameField, 1, 1);

        // Rok urodzenia
        grid.add(new Label("Rok urodzenia:"), 0, 2);
        birthYearField = new TextField();
        if (employee != null) {
            birthYearField.setText(String.valueOf(employee.getBirthYear()));
        }
        grid.add(birthYearField, 1, 2);

        // Wynagrodzenie
        grid.add(new Label("Wynagrodzenie:"), 0, 3);
        salaryField = new TextField();
        if (employee != null) {
            salaryField.setText(String.valueOf(employee.getSalary()));
        }
        grid.add(salaryField, 1, 3);

        // Stan
        grid.add(new Label("Stan:"), 0, 4);
        conditionCombo = new ComboBox<>();
        conditionCombo.getItems().addAll(EmployeeCondition.values());
        conditionCombo.setConverter(new StringConverter<EmployeeCondition>() {
            @Override
            public String toString(EmployeeCondition condition) {
                return condition.toString();
            }

            @Override
            public EmployeeCondition fromString(String string) {
                return null;
            }
        });
        if (employee != null) {
            conditionCombo.setValue(employee.getCondition());
        } else {
            conditionCombo.setValue(EmployeeCondition.OBECNY);
        }
        grid.add(conditionCombo, 1, 4);

        // Label błędów
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        grid.add(errorLabel, 0, 5, 2, 1);

        getDialogPane().setContent(grid);

        // Walidacja
        Button addButton = (Button) getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        // Listeners do walidacji
        firstNameField.textProperty().addListener((obs, oldVal, newVal) -> validateInput(addButton));
        lastNameField.textProperty().addListener((obs, oldVal, newVal) -> validateInput(addButton));
        birthYearField.textProperty().addListener((obs, oldVal, newVal) -> validateInput(addButton));
        salaryField.textProperty().addListener((obs, oldVal, newVal) -> validateInput(addButton));

        // Konwersja wyniku
        setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return createEmployee();
            }
            return null;
        });
    }

    private void validateInput(Button addButton) {
        String error = "";
        
        // Walidacja imienia
        if (firstNameField.getText().trim().isEmpty()) {
            error = "Imię nie może być puste.\n";
        }
        
        // Walidacja nazwiska
        if (lastNameField.getText().trim().isEmpty()) {
            error += "Nazwisko nie może być puste.\n";
        }
        
        // Walidacja roku urodzenia
        try {
            int year = Integer.parseInt(birthYearField.getText().trim());
            if (year < 1950 || year > 2010) {
                error += "Rok urodzenia musi być w zakresie 1950-2010.\n";
            }
        } catch (NumberFormatException e) {
            error += "Rok urodzenia musi być liczbą.\n";
        }
        
        // Walidacja wynagrodzenia
        try {
            double salary = Double.parseDouble(salaryField.getText().trim());
            if (salary <= 0) {
                error += "Wynagrodzenie musi być większe od 0.\n";
            }
        } catch (NumberFormatException e) {
            error += "Wynagrodzenie musi być liczbą.\n";
        }
        
        errorLabel.setText(error);
        addButton.setDisable(!error.isEmpty());
    }

    private Employee createEmployee() {
        return new Employee(
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                conditionCombo.getValue(),
                Integer.parseInt(birthYearField.getText().trim()),
                Double.parseDouble(salaryField.getText().trim())
        );
    }

    public static Optional<Employee> showDialog(Employee employee) {
        AddEmployeeDialog dialog = new AddEmployeeDialog(employee);
        return dialog.showAndWait();
    }
}

