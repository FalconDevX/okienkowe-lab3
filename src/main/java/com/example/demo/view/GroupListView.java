package com.example.demo.view;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class GroupListView extends ListView<String> {

    public GroupListView() {
        super();
        this.setPrefWidth(200);
    }

    public void setGroups(ObservableList<String> groups) {
        this.setItems(groups);
    }

    public String getSelectedGroup() {
        return this.getSelectionModel().getSelectedItem();
    }
}

