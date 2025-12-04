package com.example.demo.view;

import com.example.demo.model.GroupStatistics;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class GroupListView extends TableView<GroupStatistics> {

    public GroupListView() {
        super();
        this.setPrefWidth(400);
        setupColumns();
    }

    private void setupColumns() {
        // Kolumna: Nazwa grupy
        TableColumn<GroupStatistics, String> nameColumn = new TableColumn<>("Nazwa grupy");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("groupName"));
        nameColumn.setPrefWidth(150);

        // Kolumna: Liczba ocen
        TableColumn<GroupStatistics, Long> countColumn = new TableColumn<>("Liczba ocen");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("ratingCount"));
        countColumn.setPrefWidth(100);

        // Kolumna: Średnia ocena
        TableColumn<GroupStatistics, Double> avgColumn = new TableColumn<>("Średnia ocena");
        avgColumn.setCellValueFactory(new PropertyValueFactory<>("averageRating"));
        avgColumn.setPrefWidth(120);
        avgColumn.setCellFactory(column -> new javafx.scene.control.TableCell<GroupStatistics, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });

        this.getColumns().add(nameColumn);
        this.getColumns().add(countColumn);
        this.getColumns().add(avgColumn);
    }

    public void setGroups(ObservableList<GroupStatistics> groups) {
        this.setItems(groups);
    }

    public String getSelectedGroup() {
        GroupStatistics selected = this.getSelectionModel().getSelectedItem();
        return selected != null ? selected.getGroupName() : null;
    }
}

