package com.example.demo.view;

import com.example.demo.model.Employee;
import com.example.demo.model.EmployeeCondition;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class ContextMenuBuilder {
    private final TableView<Employee> tableView;
    private final Runnable onEdit;
    private final Runnable onDelete;
    private final Runnable onChangeCondition;
    private final Runnable onRaiseSalary;
    private final Runnable onShowDetails;

    public ContextMenuBuilder(TableView<Employee> tableView,
                              Runnable onEdit,
                              Runnable onDelete,
                              Runnable onChangeCondition,
                              Runnable onRaiseSalary,
                              Runnable onShowDetails) {
        this.tableView = tableView;
        this.onEdit = onEdit;
        this.onDelete = onDelete;
        this.onChangeCondition = onChangeCondition;
        this.onRaiseSalary = onRaiseSalary;
        this.onShowDetails = onShowDetails;
        setupContextMenu();
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        // Edytuj
        MenuItem editItem = new MenuItem("Edytuj");
        editItem.setOnAction(e -> {
            if (tableView.getSelectionModel().getSelectedItem() != null) {
                onEdit.run();
            }
        });
        contextMenu.getItems().add(editItem);

        // Usuń
        MenuItem deleteItem = new MenuItem("Usuń");
        deleteItem.setOnAction(e -> {
            if (tableView.getSelectionModel().getSelectedItem() != null) {
                onDelete.run();
            }
        });
        contextMenu.getItems().add(deleteItem);

        // Zmień stan - submenu
        Menu changeConditionMenu = new Menu("Zmień stan");
        for (EmployeeCondition condition : EmployeeCondition.values()) {
            MenuItem conditionItem = new MenuItem(condition.toString());
            conditionItem.setOnAction(e -> {
                Employee selected = tableView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    selected.setCondition(condition);
                    onChangeCondition.run();
                }
            });
            changeConditionMenu.getItems().add(conditionItem);
        }
        contextMenu.getItems().add(changeConditionMenu);

        // Podwyżka 10%
        MenuItem raiseItem = new MenuItem("Podwyżka 10%");
        raiseItem.setOnAction(e -> {
            if (tableView.getSelectionModel().getSelectedItem() != null) {
                onRaiseSalary.run();
            }
        });
        contextMenu.getItems().add(raiseItem);

        contextMenu.getItems().add(new SeparatorMenuItem());

        // Kopiuj dane
        MenuItem copyItem = new MenuItem("Kopiuj dane");
        copyItem.setOnAction(e -> copyToClipboard());
        contextMenu.getItems().add(copyItem);

        // Pokaż szczegóły
        MenuItem detailsItem = new MenuItem("Pokaż szczegóły");
        detailsItem.setOnAction(e -> {
            if (tableView.getSelectionModel().getSelectedItem() != null) {
                onShowDetails.run();
            }
        });
        contextMenu.getItems().add(detailsItem);

        tableView.setContextMenu(contextMenu);
    }

    private void copyToClipboard() {
        Employee selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            String data = String.format("%s %s, %s, %d, %.2f zł",
                    selected.getFirstName(),
                    selected.getLastName(),
                    selected.getCondition(),
                    selected.getBirthYear(),
                    selected.getSalary());
            content.putString(data);
            clipboard.setContent(content);
        }
    }

    public static void showDetailsDialog(Employee employee) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Szczegóły pracownika");
        alert.setHeaderText(employee.getFirstName() + " " + employee.getLastName());
        
        int currentYear = java.time.Year.now().getValue();
        int age = currentYear - employee.getBirthYear();
        
        String content = String.format(
                "Imię: %s\n" +
                "Nazwisko: %s\n" +
                "Stan: %s\n" +
                "Rok urodzenia: %d\n" +
                "Wiek: %d lat\n" +
                "Wynagrodzenie: %.2f zł",
                employee.getFirstName(),
                employee.getLastName(),
                employee.getCondition(),
                employee.getBirthYear(),
                age,
                employee.getSalary()
        );
        
        alert.setContentText(content);
        alert.showAndWait();
    }
}

