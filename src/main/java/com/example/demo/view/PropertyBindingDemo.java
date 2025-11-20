package com.example.demo.view;

import com.example.demo.model.Employee;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.Year;

public class PropertyBindingDemo extends VBox {
    private Employee demoEmployee;
    private TextField firstNameField;
    private Slider salarySlider;
    private Label employeeCountLabel;
    private ProgressBar fillProgressBar;
    private Label ageLabel;
    private Label formattedSalaryLabel;

    public PropertyBindingDemo(Employee employee, int employeeCount, int maxCapacity) {
        super(10);
        this.setPadding(new Insets(10));
        this.demoEmployee = employee;
        setupBindings(employeeCount, maxCapacity);
    }

    private void setupBindings(int employeeCount, int maxCapacity) {
        // Bidirectional binding: TextField ↔ employee.firstNameProperty()
        Label title1 = new Label("1. Dwukierunkowe wiązanie (Imię):");
        firstNameField = new TextField();
        firstNameField.textProperty().bindBidirectional(demoEmployee.firstNameProperty());
        this.getChildren().addAll(title1, firstNameField);

        // Bidirectional binding: Slider ↔ salaryProperty()
        Label title2 = new Label("2. Dwukierunkowe wiązanie (Wynagrodzenie - Slider):");
        salarySlider = new Slider(0, 20000, demoEmployee.getSalary());
        salarySlider.setShowTickLabels(true);
        salarySlider.setShowTickMarks(true);
        salarySlider.setMajorTickUnit(5000);
        
        // Konwersja między DoubleProperty a DoubleProperty przez Slider
        salarySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            demoEmployee.setSalary(newVal.doubleValue());
        });
        demoEmployee.salaryProperty().addListener((obs, oldVal, newVal) -> {
            if (Math.abs(salarySlider.getValue() - newVal.doubleValue()) > 0.01) {
                salarySlider.setValue(newVal.doubleValue());
            }
        });
        
        Label salaryValueLabel = new Label();
        salaryValueLabel.textProperty().bind(Bindings.format("%.2f zł", demoEmployee.salaryProperty()));
        this.getChildren().addAll(title2, salarySlider, salaryValueLabel);

        // Unidirectional binding: Label → liczba pracowników w grupie
        Label title3 = new Label("3. Jednokierunkowe wiązanie (Liczba pracowników):");
        employeeCountLabel = new Label();
        employeeCountLabel.setText("Liczba pracowników: " + employeeCount);
        this.getChildren().addAll(title3, employeeCountLabel);

        // Unidirectional binding: ProgressBar → zapełnienie grupy
        Label title4 = new Label("4. Jednokierunkowe wiązanie (Zapełnienie grupy):");
        fillProgressBar = new ProgressBar();
        double fillPercentage = (double) employeeCount / maxCapacity;
        fillProgressBar.setProgress(fillPercentage);
        Label fillLabel = new Label();
        fillLabel.setText(String.format("%.1f%%", fillPercentage * 100));
        this.getChildren().addAll(title4, fillProgressBar, fillLabel);

        // Computed binding: wyliczanie wieku
        Label title5 = new Label("5. Obliczane wiązanie (Wiek):");
        ageLabel = new Label();
        int currentYear = Year.now().getValue();
        StringBinding ageBinding = Bindings.createStringBinding(() -> {
            int age = currentYear - demoEmployee.getBirthYear();
            return "Wiek: " + age + " lat";
        }, demoEmployee.birthYearProperty());
        ageLabel.textProperty().bind(ageBinding);
        this.getChildren().addAll(title5, ageLabel);

        // Computed binding: formatowanie wynagrodzenia z walutą
        Label title6 = new Label("6. Obliczane wiązanie (Formatowane wynagrodzenie):");
        formattedSalaryLabel = new Label();
        StringBinding salaryBinding = Bindings.createStringBinding(() -> {
            return String.format("Wynagrodzenie: %.2f zł", demoEmployee.getSalary());
        }, demoEmployee.salaryProperty());
        formattedSalaryLabel.textProperty().bind(salaryBinding);
        this.getChildren().addAll(title6, formattedSalaryLabel);

        // Przycisk do demonstracji unbindAll()
        Label title7 = new Label("7. Odwiązanie wszystkich właściwości:");
        Button unbindButton = new Button("Odwiąż wszystkie");
        unbindButton.setOnAction(e -> unbindAll());
        this.getChildren().addAll(title7, unbindButton);
    }

    public void unbindAll() {
        firstNameField.textProperty().unbindBidirectional(demoEmployee.firstNameProperty());
        employeeCountLabel.textProperty().unbind();
        ageLabel.textProperty().unbind();
        formattedSalaryLabel.textProperty().unbind();
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Odwiązanie");
        alert.setHeaderText("Wszystkie wiązania zostały usunięte");
        alert.setContentText("Pola nie są już powiązane z właściwościami pracownika.");
        alert.showAndWait();
    }

    public void updateEmployeeCount(int count, int maxCapacity) {
        employeeCountLabel.setText("Liczba pracowników: " + count);
        double fillPercentage = (double) count / maxCapacity;
        fillProgressBar.setProgress(fillPercentage);
    }
}

