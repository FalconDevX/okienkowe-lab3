package com.example.demo;

import com.example.demo.controller.MainController;
import com.example.demo.controller.SortController;
import com.example.demo.view.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class EmployeeManagementApp extends Application {
    @Override
    public void start(Stage stage) {
        // Tworzenie komponentów GUI
        GroupListView groupListView = new GroupListView();
        EmployeeTableView employeeTableView = new EmployeeTableView();
        ControlPanel controlPanel = new ControlPanel();
        FilterPanel filterPanel = new FilterPanel();
        AdvancedFilterPanel advancedFilterPanel = new AdvancedFilterPanel();

        // Tworzenie kontrolera
        MainController controller = new MainController();
        controller.setGroupListView(groupListView);
        controller.setEmployeeTableView(employeeTableView);
        controller.setControlPanel(controlPanel);
        controller.setFilterPanel(filterPanel);
        controller.setAdvancedFilterPanel(advancedFilterPanel);
        controller.setSortController(new SortController());

        // Tworzenie głównego layoutu
        BorderPane root = new BorderPane();
        root.setLeft(groupListView);
        root.setCenter(employeeTableView);
        root.setTop(filterPanel);
        root.setRight(advancedFilterPanel);
        root.setBottom(controlPanel);

        // Tworzenie sceny
        Scene scene = new Scene(root, 1500, 700);
        controller.setScene(scene);
        
        // Zastosowanie stylów
        StyleManager.applyStyles(scene);
        
        stage.setTitle("Zarządzanie Pracownikami");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

