package com.example.demo.view;

import com.example.demo.model.Employee;
import com.example.demo.model.EmployeeCondition;
import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;

public class EmployeeTableView extends TableView<Employee> {

    public EmployeeTableView() {
        super();
        setupColumns();
    }

    private void setupColumns() {
        // Kolumna Imię
        TableColumn<Employee, String> firstNameColumn = new TableColumn<>("Imię");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameColumn.setPrefWidth(150);

        // Kolumna Nazwisko
        TableColumn<Employee, String> lastNameColumn = new TableColumn<>("Nazwisko");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastNameColumn.setPrefWidth(150);

        // Kolumna Stan
        TableColumn<Employee, EmployeeCondition> conditionColumn = new TableColumn<>("Stan");
        conditionColumn.setCellValueFactory(new PropertyValueFactory<>("condition"));
        conditionColumn.setPrefWidth(120);

        // Kolumna Rok urodzenia
        TableColumn<Employee, Integer> birthYearColumn = new TableColumn<>("Rok");
        birthYearColumn.setCellValueFactory(new PropertyValueFactory<>("birthYear"));
        birthYearColumn.setPrefWidth(80);

        // Kolumna Wynagrodzenie
        TableColumn<Employee, Double> salaryColumn = new TableColumn<>("Wynagrodzenie");
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        salaryColumn.setPrefWidth(150);

        this.getColumns().addAll(firstNameColumn, lastNameColumn, conditionColumn, birthYearColumn, salaryColumn);
    }

    public void setEmployees(ObservableList<Employee> employees) {
        this.setItems(employees);
    }

    public void makeEditable() {
        this.setEditable(true);

        // Edytowalne wynagrodzenie
        TableColumn<Employee, Double> salaryColumn = (TableColumn<Employee, Double>) this.getColumns().get(4);
        salaryColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter() {
            @Override
            public Double fromString(String value) {
                try {
                    double val = super.fromString(value);
                    if (val <= 0) {
                        throw new NumberFormatException("Wynagrodzenie musi być większe od 0");
                    }
                    return val;
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd walidacji");
                    alert.setHeaderText("Nieprawidłowa wartość wynagrodzenia");
                    alert.setContentText("Wynagrodzenie musi być liczbą większą od 0.");
                    alert.showAndWait();
                    throw e;
                }
            }
        }));

        salaryColumn.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            double newValue = event.getNewValue();
            employee.setSalary(newValue);
            animateRow(event.getTablePosition().getRow());
        });

        // Edytowalny stan
        TableColumn<Employee, EmployeeCondition> conditionColumn = (TableColumn<Employee, EmployeeCondition>) this.getColumns().get(2);
        conditionColumn.setCellFactory(ComboBoxTableCell.forTableColumn(EmployeeCondition.values()));

        conditionColumn.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            EmployeeCondition newValue = event.getNewValue();
            employee.setCondition(newValue);
            animateRow(event.getTablePosition().getRow());
        });
    }

    private void animateRow(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < this.getItems().size()) {
            FadeTransition fade = new FadeTransition(Duration.millis(200), this);
            fade.setFromValue(1.0);
            fade.setToValue(0.3);
            fade.setAutoReverse(true);
            fade.setCycleCount(2);
            fade.play();
        }
    }
}

