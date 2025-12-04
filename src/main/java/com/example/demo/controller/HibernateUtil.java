package com.example.demo.controller;

import com.example.demo.model.ClassEmployee;
import com.example.demo.model.Employee;
import com.example.demo.model.Rate;
import com.example.demo.model.AuditLog;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    static {
        try {
            // Tworzenie StandardServiceRegistry z pliku konfiguracyjnego
            StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                    .configure("hibernate.cfg.xml")
                    .build();
            
            // Tworzenie MetadataSources i dodanie klas encji programatycznie
            MetadataSources metadataSources = new MetadataSources(standardRegistry);
            metadataSources.addAnnotatedClass(ClassEmployee.class);
            metadataSources.addAnnotatedClass(Employee.class);
            metadataSources.addAnnotatedClass(Rate.class);
            metadataSources.addAnnotatedClass(AuditLog.class);
            
            // Budowanie Metadata i SessionFactory
            Metadata metadata = metadataSources.getMetadataBuilder().build();
            sessionFactory = metadata.getSessionFactoryBuilder().build();
        } catch (Exception e) {
            System.err.println("Initial SessionFactory creation failed." + e);
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}

