package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.view.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class MainController {
    private ClassContainer container;
    private ObservableList<String> groupNames;
    private ObservableList<Employee> currentEmployees;
    private FilteredList<Employee> filteredEmployees;
    private String selectedGroupName;

    private GroupListView groupListView;
    private EmployeeTableView employeeTableView;
    private ControlPanel controlPanel;
    private FilterPanel filterPanel;
    private AdvancedFilterPanel advancedFilterPanel;
    private SortController sortController;
    private Scene scene;

    public MainController() {
        this.container = new ClassContainer();
        this.groupNames = FXCollections.observableArrayList();
        this.currentEmployees = FXCollections.observableArrayList();
        this.filteredEmployees = new FilteredList<>(currentEmployees);
        initializeSampleData();
    }

    private void initializeSampleData() {
        // Tworzenie przykładowych grup
        container.addClass("Programiści", 10);
        container.addClass("Testerzy", 5);
        container.addClass("Designerzy", 8);

        // Dodanie przykładowych pracowników
        ClassEmployee grupa1 = container.getGroup("Programiści");
        grupa1.addEmployee(new Employee("Jan", "Kowalski", EmployeeCondition.OBECNY, 1990, 5000));
        grupa1.addEmployee(new Employee("Piotr", "Nowak", EmployeeCondition.DELEGACJA, 1991, 6000));
        grupa1.addEmployee(new Employee("Adam", "Kowalski", EmployeeCondition.CHORY, 1992, 7000));

        ClassEmployee grupa2 = container.getGroup("Testerzy");
        grupa2.addEmployee(new Employee("Anna", "Wiśniewska", EmployeeCondition.OBECNY, 1988, 5500));
        grupa2.addEmployee(new Employee("Maria", "Dąbrowska", EmployeeCondition.DELEGACJA, 1993, 6500));

        ClassEmployee grupa3 = container.getGroup("Designerzy");
        grupa3.addEmployee(new Employee("Katarzyna", "Lewandowska", EmployeeCondition.OBECNY, 1995, 6000));

        // Aktualizacja listy grup
        updateGroupList();
    }

    public void setGroupListView(GroupListView groupListView) {
        this.groupListView = groupListView;
        groupListView.setGroups(groupNames);
        
        // Obsługa kliknięcia w grupę
        groupListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedGroupName = newVal;
                loadEmployeesForGroup(newVal);
            }
        });
    }

    public void setEmployeeTableView(EmployeeTableView employeeTableView) {
        this.employeeTableView = employeeTableView;
        employeeTableView.setEmployees(filteredEmployees);
        employeeTableView.makeEditable();
        
        // ContextMenu
        ContextMenuBuilder contextMenu = new ContextMenuBuilder(
                employeeTableView,
                () -> handleEditEmployee(),
                () -> handleDeleteEmployee(),
                () -> loadEmployeesForGroup(selectedGroupName),
                () -> handleRaiseSalary(),
                () -> handleShowDetails()
        );
    }

    public void setControlPanel(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
        
        // Przycisk Dodaj
        controlPanel.getAddButton().setOnAction(e -> handleAddEmployee());
        
        // Przycisk Usuń
        controlPanel.getDeleteButton().setOnAction(e -> handleDeleteEmployee());
        
        // Przycisk Modyfikuj
        controlPanel.getModifyButton().setOnAction(e -> handleEditEmployee());
        
        // Przycisk Sortuj
        controlPanel.getSortButton().setOnAction(e -> handleSortEmployees());
        
        // Przycisk Statystyki
        controlPanel.getStatisticsButton().setOnAction(e -> handleShowStatistics());
        
        // Przycisk Eksport
        controlPanel.getExportButton().setOnAction(e -> handleExport());
        
        // Przycisk Import
        controlPanel.getImportButton().setOnAction(e -> handleImport());
        
        // Przycisk Motyw
        controlPanel.getThemeButton().setOnAction(e -> handleToggleTheme());
    }

    public void setFilterPanel(FilterPanel filterPanel) {
        this.filterPanel = filterPanel;
        filterPanel.setFilteredList(filteredEmployees);
    }

    public void setAdvancedFilterPanel(AdvancedFilterPanel advancedFilterPanel) {
        this.advancedFilterPanel = advancedFilterPanel;
        advancedFilterPanel.setFilteredList(filteredEmployees);
    }

    public void setSortController(SortController sortController) {
        this.sortController = sortController;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    private void updateGroupList() {
        groupNames.setAll(container.getGroupsInOrder());
    }

    private void loadEmployeesForGroup(String groupName) {
        ClassEmployee group = container.getGroup(groupName);
        if (group != null) {
            currentEmployees.setAll(group.getEmployees());
        } else {
            currentEmployees.clear();
        }
    }

    private void handleAddEmployee() {
        if (selectedGroupName == null) {
            showAlert("Brak wybranej grupy", "Proszę wybrać grupę z listy.");
            return;
        }

        ClassEmployee group = container.getGroup(selectedGroupName);
        if (group == null) {
            return;
        }

        Optional<Employee> result = AddEmployeeDialog.showDialog(null);
        if (result.isPresent()) {
            Employee newEmployee = result.get();
            try {
                if (group.addEmployee(newEmployee)) {
                    loadEmployeesForGroup(selectedGroupName);
                    showAlert("Sukces", "Pracownik został dodany.");
                } else {
                    throw new ExceptionHandler.GroupFullException("Grupa jest pełna lub pracownik już istnieje.");
                }
            } catch (Exception e) {
                ExceptionHandler.handleException(e);
            }
        }
    }

    private void handleEditEmployee() {
        if (selectedGroupName == null) {
            showAlert("Brak wybranej grupy", "Proszę wybrać grupę z listy.");
            return;
        }

        Employee selected = employeeTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Brak wyboru", "Proszę wybrać pracownika do edycji.");
            return;
        }

        Optional<Employee> result = AddEmployeeDialog.showDialog(selected);
        if (result.isPresent()) {
            Employee edited = result.get();
            selected.setFirstName(edited.getFirstName());
            selected.setLastName(edited.getLastName());
            selected.setCondition(edited.getCondition());
            selected.setBirthYear(edited.getBirthYear());
            selected.setSalary(edited.getSalary());
            loadEmployeesForGroup(selectedGroupName);
            showAlert("Sukces", "Pracownik został zaktualizowany.");
        }
    }

    private void handleDeleteEmployee() {
        if (selectedGroupName == null) {
            showAlert("Brak wybranej grupy", "Proszę wybrać grupę z listy.");
            return;
        }

        Employee selected = employeeTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Brak wyboru", "Proszę wybrać pracownika do usunięcia.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Usuń pracownika");
        confirmDialog.setHeaderText("Czy na pewno chcesz usunąć pracownika?");
        confirmDialog.setContentText(selected.getFirstName() + " " + selected.getLastName());
        Optional<ButtonType> result = confirmDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            ClassEmployee group = container.getGroup(selectedGroupName);
            if (group != null && group.removeEmployee(selected)) {
                loadEmployeesForGroup(selectedGroupName);
                showAlert("Sukces", "Pracownik został usunięty.");
            } else {
                showAlert("Błąd", "Nie udało się usunąć pracownika.");
            }
        }
    }

    private void handleRaiseSalary() {
        Employee selected = employeeTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            double newSalary = selected.getSalary() * 1.1;
            selected.setSalary(newSalary);
            loadEmployeesForGroup(selectedGroupName);
        }
    }

    private void handleShowDetails() {
        Employee selected = employeeTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            ContextMenuBuilder.showDetailsDialog(selected);
        }
    }

    private void handleSortEmployees() {
        if (selectedGroupName == null) {
            showAlert("Brak wybranej grupy", "Proszę wybrać grupę z listy.");
            return;
        }

        if (sortController == null) {
            sortController = new SortController();
        }

        ChoiceDialog<String> sortDialog = new ChoiceDialog<>(
                "Po nazwisku",
                "Po nazwisku", "Po pensji", "Po wieku", "Po stanie", "Wielokryterialne"
        );
        sortDialog.setTitle("Sortuj pracowników");
        sortDialog.setHeaderText("Wybierz typ sortowania");
        sortDialog.setContentText("Sortowanie:");
        Optional<String> result = sortDialog.showAndWait();

        if (result.isPresent()) {
            sortController.clearIndicators(employeeTableView);
            switch (result.get()) {
                case "Po nazwisku":
                    sortController.sortByLastName(currentEmployees, employeeTableView);
                    break;
                case "Po pensji":
                    sortController.sortBySalary(currentEmployees, employeeTableView);
                    break;
                case "Po wieku":
                    sortController.sortByAge(currentEmployees, employeeTableView);
                    break;
                case "Po stanie":
                    sortController.sortByCondition(currentEmployees, employeeTableView);
                    break;
                case "Wielokryterialne":
                    sortController.sortMultiCriteria(currentEmployees, employeeTableView, true, true);
                    break;
            }
        }
    }

    private void handleShowStatistics() {
        if (selectedGroupName == null) {
            showAlert("Brak wybranej grupy", "Proszę wybrać grupę z listy.");
            return;
        }

        ClassEmployee group = container.getGroup(selectedGroupName);
        if (group == null) {
            return;
        }

        GroupStatisticsView statsView = new GroupStatisticsView(group);
        Stage statsStage = new Stage();
        statsStage.setTitle("Statystyki - " + selectedGroupName);
        statsStage.setScene(new javafx.scene.Scene(statsView, 900, 600));
        statsStage.show();
    }

    private void handleExport() {
        if (selectedGroupName == null) {
            showAlert("Brak wybranej grupy", "Proszę wybrać grupę z listy.");
            return;
        }

        ClassEmployee group = container.getGroup(selectedGroupName);
        if (group == null) {
            return;
        }

        FileChooser fileChooser = DataPersistence.createFileChooser(
                "Eksportuj do CSV", "*.csv", "Pliki CSV"
        );
        Stage stage = (Stage) employeeTableView.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            DataPersistence.exportToCSV(group, file);
        }
    }

    private void handleImport() {
        FileChooser fileChooser = DataPersistence.createFileChooser(
                "Importuj z JSON", "*.json", "Pliki JSON"
        );
        Stage stage = (Stage) employeeTableView.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                ClassContainer imported = DataPersistence.importFromJSON(file);
                showAlert("Sukces", "Dane zostały zaimportowane.");
            } catch (Exception e) {
                ExceptionHandler.handleException(e);
            }
        }
    }

    private void handleToggleTheme() {
        if (scene != null) {
            StyleManager.toggleTheme(scene);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

