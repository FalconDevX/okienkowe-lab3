package com.example.demo.view;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ControlPanel extends HBox {
    private Button addButton;
    private Button deleteButton;
    private Button modifyButton;
    private Button sortButton;
    private Button statisticsButton;
    private Button exportButton;
    private Button importButton;
    private Button themeButton;

    public ControlPanel() {
        super(10);
        setupButtons();
        this.getChildren().addAll(addButton, deleteButton, modifyButton, sortButton, 
                statisticsButton, exportButton, importButton, themeButton);
        this.setStyle("-fx-padding: 10;");
    }

    private void setupButtons() {
        addButton = new Button("Dodaj");
        deleteButton = new Button("Usuń");
        modifyButton = new Button("Modyfikuj");
        sortButton = new Button("Sortuj");
        statisticsButton = new Button("Statystyki");
        exportButton = new Button("Eksport");
        importButton = new Button("Import");
        themeButton = new Button("Motyw");

        // Ustawienie szerokości przycisków
        addButton.setPrefWidth(100);
        deleteButton.setPrefWidth(100);
        modifyButton.setPrefWidth(100);
        sortButton.setPrefWidth(100);
        statisticsButton.setPrefWidth(100);
        exportButton.setPrefWidth(100);
        importButton.setPrefWidth(100);
        themeButton.setPrefWidth(100);
    }

    public Button getAddButton() {
        return addButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public Button getModifyButton() {
        return modifyButton;
    }

    public Button getSortButton() {
        return sortButton;
    }

    public Button getStatisticsButton() {
        return statisticsButton;
    }

    public Button getExportButton() {
        return exportButton;
    }

    public Button getImportButton() {
        return importButton;
    }

    public Button getThemeButton() {
        return themeButton;
    }
}

