package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.view.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class MainController {
    private ClassContainer container; // Zachowane dla kompatybilności z niektórymi metodami
    private ClassEmployeeDAO classEmployeeDAO;
    private EmployeeDAO employeeDAO;
    private RateDAO rateDAO;
    private CriteriaStatisticsDAO criteriaStatisticsDAO;
    private ObservableList<GroupStatistics> groupStatistics;
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
        this.classEmployeeDAO = new ClassEmployeeDAO();
        this.employeeDAO = new EmployeeDAO();
        this.rateDAO = new RateDAO();
        this.criteriaStatisticsDAO = new CriteriaStatisticsDAO();
        this.groupStatistics = FXCollections.observableArrayList();
        this.currentEmployees = FXCollections.observableArrayList();
        this.filteredEmployees = new FilteredList<>(currentEmployees);
        initializeSampleData();
    }

    private void initializeSampleData() {
        Task<ConnectionStatus> initTask = new Task<ConnectionStatus>() {
            @Override
            protected ConnectionStatus call() throws Exception {
                try {
                    // Test połączenia z bazą danych
                    List<ClassEmployee> existingGroups = classEmployeeDAO.findAll();
                    int groupCount = existingGroups.size();
                    int employeeCount = 0;
                    int rateCount = 0;
                    
                    // Policz pracowników i oceny
                    if (groupCount > 0) {
                        List<Employee> allEmployees = new ArrayList<>();
                        for (ClassEmployee group : existingGroups) {
                            List<Employee> employees = classEmployeeDAO.getEmployeesByGroupName(group.getGroupName());
                            allEmployees.addAll(employees);
                        }
                        employeeCount = allEmployees.size();
                        rateCount = rateDAO.findAll().size();
                    }
                    
                    if (existingGroups.isEmpty()) {
                        // Tworzenie przykładowych grup w bazie
                        ClassEmployee grupa1 = new ClassEmployee("Programiści", 10);
                        classEmployeeDAO.save(grupa1);
                        
                        ClassEmployee grupa2 = new ClassEmployee("Testerzy", 5);
                        classEmployeeDAO.save(grupa2);
                        
                        ClassEmployee grupa3 = new ClassEmployee("Designerzy", 8);
                        classEmployeeDAO.save(grupa3);

                        // Dodanie przykładowych pracowników
                        Employee emp1 = new Employee("Jan", "Kowalski", EmployeeCondition.OBECNY, 1990, 5000);
                        emp1.setGroup(grupa1);
                        employeeDAO.save(emp1);
                        
                        Employee emp2 = new Employee("Piotr", "Nowak", EmployeeCondition.DELEGACJA, 1991, 6000);
                        emp2.setGroup(grupa1);
                        employeeDAO.save(emp2);
                        
                        Employee emp3 = new Employee("Adam", "Kowalski", EmployeeCondition.CHORY, 1992, 7000);
                        emp3.setGroup(grupa1);
                        employeeDAO.save(emp3);

                        Employee emp4 = new Employee("Anna", "Wiśniewska", EmployeeCondition.OBECNY, 1988, 5500);
                        emp4.setGroup(grupa2);
                        employeeDAO.save(emp4);
                        
                        Employee emp5 = new Employee("Maria", "Dąbrowska", EmployeeCondition.DELEGACJA, 1993, 6500);
                        emp5.setGroup(grupa2);
                        employeeDAO.save(emp5);

                        Employee emp6 = new Employee("Katarzyna", "Lewandowska", EmployeeCondition.OBECNY, 1995, 6000);
                        emp6.setGroup(grupa3);
                        employeeDAO.save(emp6);
                        
                        groupCount = 3;
                        employeeCount = 6;
                        rateCount = 0;
                    }
                    
                    // Załaduj grupy z bazy
                    javafx.application.Platform.runLater(() -> {
                        updateGroupList();
                    });
                    
                    return new ConnectionStatus(true, "Połączenie z bazą danych nawiązane pomyślnie!", 
                            groupCount, employeeCount, rateCount, null);
                } catch (Exception e) {
                    return new ConnectionStatus(false, "Błąd połączenia z bazą danych!", 
                            0, 0, 0, e.getMessage());
                }
            }
        };
        
        initTask.setOnSucceeded(e -> {
            ConnectionStatus status = initTask.getValue();
            showConnectionStatus(status);
        });
        
        initTask.setOnFailed(e -> {
            ConnectionStatus status = new ConnectionStatus(false, "Błąd podczas inicjalizacji!", 
                    0, 0, 0, initTask.getException().getMessage());
            showConnectionStatus(status);
        });
        
        new Thread(initTask).start();
    }
    
    private void showConnectionStatus(ConnectionStatus status) {
        Alert alert = new Alert(status.isSuccess() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle("Status połączenia z bazą danych");
        alert.setHeaderText(status.getMessage());
        
        StringBuilder content = new StringBuilder();
        if (status.isSuccess()) {
            content.append("✅ Połączenie z bazą danych działa poprawnie!\n\n");
            content.append("📊 Statystyki bazy danych:\n");
            content.append("   • Grupy: ").append(status.getGroupCount()).append("\n");
            content.append("   • Pracownicy: ").append(status.getEmployeeCount()).append("\n");
            content.append("   • Oceny: ").append(status.getRateCount()).append("\n");
            if (status.getGroupCount() == 0) {
                content.append("\n⚠️ Baza danych jest pusta. Dodano przykładowe dane.");
            }
        } else {
            content.append("❌ Nie udało się połączyć z bazą danych.\n\n");
            content.append("Szczegóły błędu:\n");
            content.append(status.getErrorMessage() != null ? status.getErrorMessage() : "Nieznany błąd");
            content.append("\n\nSprawdź:\n");
            content.append("• Czy MySQL jest uruchomiony\n");
            content.append("• Czy dane w hibernate.cfg.xml są poprawne\n");
            content.append("• Czy masz dostęp do internetu (baza w chmurze)");
        }
        
        alert.setContentText(content.toString());
        alert.showAndWait();
    }
    
    // Klasa pomocnicza do przechowywania statusu połączenia
    private static class ConnectionStatus {
        private final boolean success;
        private final String message;
        private final int groupCount;
        private final int employeeCount;
        private final int rateCount;
        private final String errorMessage;
        
        public ConnectionStatus(boolean success, String message, int groupCount, 
                              int employeeCount, int rateCount, String errorMessage) {
            this.success = success;
            this.message = message;
            this.groupCount = groupCount;
            this.employeeCount = employeeCount;
            this.rateCount = rateCount;
            this.errorMessage = errorMessage;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getGroupCount() { return groupCount; }
        public int getEmployeeCount() { return employeeCount; }
        public int getRateCount() { return rateCount; }
        public String getErrorMessage() { return errorMessage; }
    }

    public void setGroupListView(GroupListView groupListView) {
        this.groupListView = groupListView;
        groupListView.setGroups(groupStatistics);
        
        // Obsługa kliknięcia w grupę
        groupListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedGroupName = newVal.getGroupName();
                loadEmployeesForGroup(selectedGroupName);
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
        Task<javafx.collections.ObservableList<GroupStatistics>> task = new Task<javafx.collections.ObservableList<GroupStatistics>>() {
            @Override
            protected javafx.collections.ObservableList<GroupStatistics> call() throws Exception {
                // Pobierz wszystkie grupy
                List<String> groupNames = classEmployeeDAO.findAllGroupNames();
                
                // Pobierz statystyki ocen używając Criteria API
                Map<String, CriteriaStatisticsDAO.GroupRatingStats> stats = 
                    criteriaStatisticsDAO.getGroupRatingStatistics();
                
                // Utwórz listę statystyk dla wszystkich grup
                javafx.collections.ObservableList<GroupStatistics> result = FXCollections.observableArrayList();
                for (String groupName : groupNames) {
                    CriteriaStatisticsDAO.GroupRatingStats groupStats = stats.get(groupName);
                    if (groupStats != null) {
                        result.add(new GroupStatistics(groupName, groupStats.getCount(), groupStats.getAverage()));
                    } else {
                        // Grupa bez ocen
                        result.add(new GroupStatistics(groupName, 0L, 0.0));
                    }
                }
                
                return result;
            }
        };
        
        task.setOnSucceeded(e -> {
            groupStatistics.setAll(task.getValue());
        });
        
        task.setOnFailed(e -> {
            ExceptionHandler.handleException(task.getException());
        });
        
        new Thread(task).start();
    }

    private void loadEmployeesForGroup(String groupName) {
        Task<List<Employee>> task = new Task<List<Employee>>() {
            @Override
            protected List<Employee> call() throws Exception {
                return classEmployeeDAO.getEmployeesByGroupName(groupName);
            }
        };
        
        task.setOnSucceeded(e -> {
            currentEmployees.setAll(task.getValue());
        });
        
        task.setOnFailed(e -> {
            ExceptionHandler.handleException(task.getException());
            currentEmployees.clear();
        });
        
        new Thread(task).start();
    }

    private void handleAddEmployee() {
        if (selectedGroupName == null) {
            showAlert("Brak wybranej grupy", "Proszę wybrać grupę z listy.");
            return;
        }

        Optional<Employee> result = AddEmployeeDialog.showDialog(null);
        if (result.isPresent()) {
            Employee newEmployee = result.get();
            
            Task<Boolean> task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    // Sprawdź czy pracownik już istnieje
                    if (employeeDAO.employeeExists(newEmployee.getFirstName(), 
                            newEmployee.getLastName(), selectedGroupName)) {
                        throw new ExceptionHandler.EmployeeAlreadyExistsException(
                                "Pracownik o tym imieniu i nazwisku już istnieje w grupie.");
                    }
                    
                    // Sprawdź pojemność grupy
                    ClassEmployee group = classEmployeeDAO.findByName(selectedGroupName);
                    if (group == null) {
                        throw new ExceptionHandler.GroupNotFoundException("Grupa nie została znaleziona.");
                    }
                    
                    List<Employee> existingEmployees = classEmployeeDAO.getEmployeesByGroupName(selectedGroupName);
                    if (existingEmployees.size() >= group.getMaxCapacity()) {
                        throw new ExceptionHandler.GroupFullException("Grupa jest pełna.");
                    }
                    
                    // Dodaj pracownika
                    newEmployee.setGroup(group);
                    employeeDAO.save(newEmployee);
                    return true;
                }
            };
            
            task.setOnSucceeded(e -> {
                loadEmployeesForGroup(selectedGroupName);
                showAlert("Sukces", "Pracownik został dodany.");
            });
            
            task.setOnFailed(e -> {
                ExceptionHandler.handleException(task.getException());
            });
            
            new Thread(task).start();
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
            
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    selected.setFirstName(edited.getFirstName());
                    selected.setLastName(edited.getLastName());
                    selected.setCondition(edited.getCondition());
                    selected.setBirthYear(edited.getBirthYear());
                    selected.setSalary(edited.getSalary());
                    employeeDAO.update(selected);
                    return null;
                }
            };
            
            task.setOnSucceeded(e -> {
                loadEmployeesForGroup(selectedGroupName);
                showAlert("Sukces", "Pracownik został zaktualizowany.");
            });
            
            task.setOnFailed(e -> {
                ExceptionHandler.handleException(task.getException());
            });
            
            new Thread(task).start();
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
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    employeeDAO.delete(selected);
                    return null;
                }
            };
            
            task.setOnSucceeded(e -> {
                loadEmployeesForGroup(selectedGroupName);
                showAlert("Sukces", "Pracownik został usunięty.");
            });
            
            task.setOnFailed(e -> {
                ExceptionHandler.handleException(task.getException());
            });
            
            new Thread(task).start();
        }
    }

    private void handleRaiseSalary() {
        Employee selected = employeeTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            double newSalary = selected.getSalary() * 1.1;
            
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    selected.setSalary(newSalary);
                    employeeDAO.update(selected);
                    return null;
                }
            };
            
            task.setOnSucceeded(e -> {
                loadEmployeesForGroup(selectedGroupName);
            });
            
            task.setOnFailed(e -> {
                ExceptionHandler.handleException(task.getException());
            });
            
            new Thread(task).start();
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

        Task<ClassEmployee> task = new Task<ClassEmployee>() {
            @Override
            protected ClassEmployee call() throws Exception {
                ClassEmployee group = classEmployeeDAO.findByName(selectedGroupName);
                if (group != null) {
                    // Załaduj pracowników
                    List<Employee> employees = classEmployeeDAO.getEmployeesByGroupName(selectedGroupName);
                    group.setEmployees(employees);
                }
                return group;
            }
        };
        
        task.setOnSucceeded(e -> {
            ClassEmployee group = task.getValue();
            if (group != null) {
                GroupStatisticsView statsView = new GroupStatisticsView(group);
                Stage statsStage = new Stage();
                statsStage.setTitle("Statystyki - " + selectedGroupName);
                statsStage.setScene(new javafx.scene.Scene(statsView, 900, 600));
                statsStage.show();
            }
        });
        
        task.setOnFailed(e -> {
            ExceptionHandler.handleException(task.getException());
        });
        
        new Thread(task).start();
    }

    private void handleExport() {
        if (selectedGroupName == null) {
            showAlert("Brak wybranej grupy", "Proszę wybrać grupę z listy.");
            return;
        }

        FileChooser fileChooser = DataPersistence.createFileChooser(
                "Eksportuj do CSV", "*.csv", "Pliki CSV"
        );
        Stage stage = (Stage) employeeTableView.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            DataPersistence.exportToCSV(selectedGroupName, file, employeeDAO);
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

