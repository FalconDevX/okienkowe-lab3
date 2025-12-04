package com.example.demo.controller;

import com.example.demo.model.*;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DataPersistence {
    private static final String BACKUP_DIR = "backups";
    private static final int AUTOSAVE_INTERVAL_MINUTES = 5;

    public static void exportToJSON(ClassContainer container, File file) {
        Task<Void> exportTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Eksportowanie do JSON...");
                updateProgress(0, 100);

                StringBuilder json = new StringBuilder();
                json.append("{\n");
                json.append("  \"groups\": [\n");

                List<String> groupNames = container.getGroupsInOrder();
                for (int i = 0; i < groupNames.size(); i++) {
                    String groupName = groupNames.get(i);
                    ClassEmployee group = container.getGroup(groupName);
                    
                    json.append("    {\n");
                    json.append("      \"name\": \"").append(escapeJson(groupName)).append("\",\n");
                    json.append("      \"maxCapacity\": ").append(group.getMaxCapacity()).append(",\n");
                    json.append("      \"employees\": [\n");

                    List<Employee> employees = group.getEmployees();
                    for (int j = 0; j < employees.size(); j++) {
                        Employee emp = employees.get(j);
                        json.append("        {\n");
                        json.append("          \"firstName\": \"").append(escapeJson(emp.getFirstName())).append("\",\n");
                        json.append("          \"lastName\": \"").append(escapeJson(emp.getLastName())).append("\",\n");
                        json.append("          \"condition\": \"").append(emp.getCondition().name()).append("\",\n");
                        json.append("          \"birthYear\": ").append(emp.getBirthYear()).append(",\n");
                        json.append("          \"salary\": ").append(emp.getSalary());
                        json.append(j < employees.size() - 1 ? "\n        },\n" : "\n        }\n");
                    }

                    json.append("      ]\n");
                    json.append(i < groupNames.size() - 1 ? "    },\n" : "    }\n");
                    updateProgress((i + 1) * 100 / groupNames.size(), 100);
                }

                json.append("  ]\n");
                json.append("}\n");

                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(json.toString());
                }

                return null;
            }
        };

        showProgressDialog(exportTask, "Eksport do JSON");
    }

    public static void exportToCSV(String groupName, File file, EmployeeDAO employeeDAO) {
        Task<Void> exportTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Eksportowanie do CSV...");
                updateProgress(0, 100);

                try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                    writer.println("Imię,Nazwisko,Stan,Rok urodzenia,Wynagrodzenie");
                    
                    // Użycie HQL do pobrania danych z bazy
                    org.hibernate.Session session = com.example.demo.controller.HibernateUtil.getSessionFactory().openSession();
                    try {
                        org.hibernate.query.Query<Employee> query = session.createQuery(
                                "FROM Employee e WHERE e.group.groupName = :groupName ORDER BY e.lastName", 
                                Employee.class);
                        query.setParameter("groupName", groupName);
                        List<Employee> employees = query.list();
                        
                        for (int i = 0; i < employees.size(); i++) {
                            Employee emp = employees.get(i);
                            writer.printf("%s,%s,%s,%d,%.2f%n",
                                    emp.getFirstName(),
                                    emp.getLastName(),
                                    emp.getCondition(),
                                    emp.getBirthYear(),
                                    emp.getSalary());
                            updateProgress((i + 1) * 100 / employees.size(), 100);
                        }
                    } finally {
                        session.close();
                    }
                }

                return null;
            }
        };

        showProgressDialog(exportTask, "Eksport do CSV");
    }

    public static ClassContainer importFromJSON(File file) throws IOException {
        ClassContainer container = new ClassContainer();
        
        StringBuilder json = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
        }

        // Prosty parser JSON (dla uproszczenia - w produkcji użyj biblioteki jak Gson)
        // Parsowanie (uproszczone - w rzeczywistości użyj biblioteki JSON)
        // Tutaj tylko szkielet - pełna implementacja wymagałaby biblioteki JSON
        
        return container;
    }

    public static FileChooser createFileChooser(String title, String extension, String description) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, extension));
        return fileChooser;
    }

    private static void showProgressDialog(Task<Void> task, String title) {
        Alert progressAlert = new Alert(Alert.AlertType.INFORMATION);
        progressAlert.setTitle(title);
        progressAlert.setHeaderText("Proszę czekać...");
        
        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(0);
        progressBar.progressProperty().bind(task.progressProperty());
        
        progressAlert.getDialogPane().setContent(progressBar);
        progressAlert.show();

        task.setOnSucceeded(e -> {
            progressAlert.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sukces");
            alert.setHeaderText("Eksport zakończony");
            alert.setContentText("Dane zostały pomyślnie wyeksportowane.");
            alert.showAndWait();
        });

        task.setOnFailed(e -> {
            progressAlert.close();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Eksport nie powiódł się");
            alert.setContentText(task.getException().getMessage());
            alert.showAndWait();
        });

        new Thread(task).start();
    }

    public static void setupAutosave(ClassContainer container) {
        // W rzeczywistej implementacji użyj ScheduledExecutorService
        // Tutaj tylko szkielet
    }

    public static void createBackup(ClassContainer container) {
        try {
            Path backupPath = Paths.get(BACKUP_DIR);
            if (!Files.exists(backupPath)) {
                Files.createDirectories(backupPath);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String backupFileName = "backup_" + LocalDateTime.now().format(formatter) + ".json";
            File backupFile = backupPath.resolve(backupFileName).toFile();

            exportToJSON(container, backupFile);
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }

    private static String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}

