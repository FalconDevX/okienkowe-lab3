package com.example.demo.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ExceptionHandler {
    private static final String LOG_FILE = "exceptions.log";

    public static void handleException(Exception e) {
        logException(e);
        showErrorAlert(e);
    }

    private static void logException(Exception e) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            writer.println("[" + LocalDateTime.now().format(formatter) + "] " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace(writer);
            writer.println();
        } catch (IOException ioException) {
            System.err.println("Nie udało się zapisać do pliku logów: " + ioException.getMessage());
        }
    }

    private static void showErrorAlert(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        
        String message = "";
        String header = "Wystąpił błąd";
        
        if (e instanceof EmployeeAlreadyExistsException) {
            header = "Pracownik już istnieje";
            message = "Pracownik o podanym imieniu i nazwisku już istnieje w grupie.";
        } else if (e instanceof GroupFullException) {
            header = "Grupa pełna";
            message = "Grupa osiągnęła maksymalną pojemność. Nie można dodać więcej pracowników.";
        } else if (e instanceof InvalidEmployeeDataException) {
            header = "Nieprawidłowe dane";
            message = "Wprowadzone dane pracownika są nieprawidłowe: " + e.getMessage();
        } else if (e instanceof GroupNotFoundException) {
            header = "Grupa nie znaleziona";
            message = "Nie znaleziono grupy o podanej nazwie.";
        } else {
            header = "Nieoczekiwany błąd";
            message = "Wystąpił nieoczekiwany błąd: " + e.getMessage();
        }
        
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static Optional<ButtonType> showConfirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potwierdzenie");
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }

    // Wyjątki niestandardowe
    public static class EmployeeAlreadyExistsException extends Exception {
        public EmployeeAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class GroupFullException extends Exception {
        public GroupFullException(String message) {
            super(message);
        }
    }

    public static class InvalidEmployeeDataException extends Exception {
        public InvalidEmployeeDataException(String message) {
            super(message);
        }
    }

    public static class GroupNotFoundException extends Exception {
        public GroupNotFoundException(String message) {
            super(message);
        }
    }
}

