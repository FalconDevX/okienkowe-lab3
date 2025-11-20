package com.example.demo.view;

import com.example.demo.model.ClassEmployee;
import com.example.demo.model.Employee;
import com.example.demo.model.EmployeeCondition;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GroupStatisticsView extends BorderPane {
    private ClassEmployee group;
    private PieChart pieChart;
    private BarChart<String, Number> barChart;
    private LineChart<String, Number> lineChart;
    private VBox statisticsBox;
    private BorderPane chartContainer;

    public GroupStatisticsView(ClassEmployee group) {
        this.group = group;
        setupUI();
        updateStatistics();
    }

    private void setupUI() {
        // Przyciski przełączania widoków
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        
        Button pieChartButton = new Button("Wykres kołowy");
        Button barChartButton = new Button("Wykres słupkowy");
        Button lineChartButton = new Button("Wykres liniowy");
        Button exportButton = new Button("Eksportuj statystyki");

        pieChartButton.setOnAction(e -> showPieChart());
        barChartButton.setOnAction(e -> showBarChart());
        lineChartButton.setOnAction(e -> showLineChart());
        exportButton.setOnAction(e -> exportStatistics());

        buttonBox.getChildren().addAll(pieChartButton, barChartButton, lineChartButton, exportButton);
        this.setTop(buttonBox);

        // Kontener na wykresy
        chartContainer = new BorderPane();
        this.setCenter(chartContainer);

        // Panel statystyk
        statisticsBox = new VBox(10);
        statisticsBox.setPadding(new Insets(10));
        statisticsBox.setPrefWidth(200);
        this.setRight(statisticsBox);

        // Domyślnie pokaż wykres kołowy
        showPieChart();
    }

    private void showPieChart() {
        pieChart = new PieChart();
        updatePieChart();
        chartContainer.setCenter(pieChart);
    }

    private void showBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Rozkład wynagrodzeń");
        updateBarChart();
        chartContainer.setCenter(barChart);
    }

    private void showLineChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Średnie wynagrodzenie w czasie (symulacja)");
        updateLineChart();
        chartContainer.setCenter(lineChart);
    }

    private void updatePieChart() {
        pieChart.getData().clear();
        pieChart.setTitle("Rozkład stanów pracowników");

        Map<EmployeeCondition, Integer> counts = new HashMap<>();
        for (EmployeeCondition cond : EmployeeCondition.values()) {
            counts.put(cond, 0);
        }

        for (Employee emp : group.getEmployees()) {
            counts.put(emp.getCondition(), counts.get(emp.getCondition()) + 1);
        }

        for (Map.Entry<EmployeeCondition, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > 0) {
                pieChart.getData().add(new PieChart.Data(entry.getKey().toString(), entry.getValue()));
            }
        }
    }

    private void updateBarChart() {
        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Liczba pracowników");

        // Przedziały wynagrodzeń co 1000 zł
        Map<String, Integer> ranges = new HashMap<>();
        for (Employee emp : group.getEmployees()) {
            int range = (int) (Math.floor(emp.getSalary() / 1000) * 1000);
            String rangeKey = range + "-" + (range + 1000);
            ranges.put(rangeKey, ranges.getOrDefault(rangeKey, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : ranges.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);
    }

    private void updateLineChart() {
        lineChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Średnie wynagrodzenie");

        // Symulacja danych w czasie (ostatnie 6 miesięcy)
        double avgSalary = group.getEmployees().stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);

        String[] months = {"Sty", "Lut", "Mar", "Kwi", "Maj", "Cze"};
        for (int i = 0; i < months.length; i++) {
            // Symulacja: niewielkie wahania wokół średniej
            double value = avgSalary + (Math.random() - 0.5) * 500;
            series.getData().add(new XYChart.Data<>(months[i], value));
        }

        lineChart.getData().add(series);
    }

    private void updateStatistics() {
        statisticsBox.getChildren().clear();

        ObservableList<Employee> employees = javafx.collections.FXCollections.observableArrayList(group.getEmployees());
        int count = employees.size();
        double avgSalary = employees.stream().mapToDouble(Employee::getSalary).average().orElse(0.0);
        double median = group.medianSalary();
        int maxCapacity = group.getMaxCapacity();
        double fillPercentage = (count * 100.0) / maxCapacity;

        statisticsBox.getChildren().addAll(
                new Label("Statystyki grupy:"),
                new Label("Nazwa: " + group.getGroupName()),
                new Label("Liczba pracowników: " + count),
                new Label("Średnia płaca: " + String.format("%.2f", avgSalary) + " zł"),
                new Label("Mediana płacy: " + String.format("%.2f", median) + " zł"),
                new Label("Zapełnienie: " + String.format("%.1f", fillPercentage) + "%")
        );
    }

    private void exportStatistics() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Eksportuj statystyki");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki tekstowe", "*.txt"));
        fileChooser.setInitialFileName("statystyki_" + group.getGroupName() + ".txt");

        Stage stage = (Stage) this.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("STATYSTYKI GRUPY: " + group.getGroupName() + "\n");
                writer.write("================================\n\n");
                
                ObservableList<Employee> employees = javafx.collections.FXCollections.observableArrayList(group.getEmployees());
                writer.write("Liczba pracowników: " + employees.size() + "\n");
                
                double avgSalary = employees.stream().mapToDouble(Employee::getSalary).average().orElse(0.0);
                writer.write("Średnia płaca: " + String.format("%.2f", avgSalary) + " zł\n");
                
                double median = group.medianSalary();
                writer.write("Mediana płacy: " + String.format("%.2f", median) + " zł\n");
                
                int maxCapacity = group.getMaxCapacity();
                double fillPercentage = (employees.size() * 100.0) / maxCapacity;
                writer.write("Zapełnienie: " + String.format("%.1f", fillPercentage) + "%\n\n");
                
                writer.write("Rozkład stanów:\n");
                Map<EmployeeCondition, Integer> counts = new HashMap<>();
                for (EmployeeCondition cond : EmployeeCondition.values()) {
                    counts.put(cond, 0);
                }
                for (Employee emp : employees) {
                    counts.put(emp.getCondition(), counts.get(emp.getCondition()) + 1);
                }
                for (Map.Entry<EmployeeCondition, Integer> entry : counts.entrySet()) {
                    writer.write("  " + entry.getKey() + ": " + entry.getValue() + "\n");
                }

                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Eksport");
                alert.setHeaderText("Sukces");
                alert.setContentText("Statystyki zostały wyeksportowane do pliku.");
                alert.showAndWait();
            } catch (IOException e) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Nie udało się wyeksportować statystyk");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }

    public void refresh() {
        updateStatistics();
        if (pieChart != null) updatePieChart();
        if (barChart != null) updateBarChart();
        if (lineChart != null) updateLineChart();
    }
}

