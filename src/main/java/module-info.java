module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.naming;
    requires java.sql;
    requires org.slf4j;

    opens com.example.demo to javafx.fxml;
    opens com.example.demo.model to javafx.base, org.hibernate.orm.core, jakarta.persistence;
    opens com.example.demo.controller to org.hibernate.orm.core;
    opens com.example.demo.service to org.hibernate.orm.core;
    exports com.example.demo;
    exports com.example.demo.model;
    exports com.example.demo.view;
    exports com.example.demo.controller;
    exports com.example.demo.service;
}