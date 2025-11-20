package com.example.demo.view;

import javafx.scene.Scene;

public class StyleManager {
    public enum Theme {
        LIGHT, DARK
    }

    private static Theme currentTheme = Theme.LIGHT;
    private static final String CSS_PATH = "/styles.css";

    public static void applyTheme(Scene scene, Theme theme) {
        currentTheme = theme;
        
        // Usuń poprzedni motyw
        scene.getRoot().getStyleClass().removeAll("dark-theme", "light-theme");
        
        // Dodaj nowy motyw
        if (theme == Theme.DARK) {
            scene.getRoot().getStyleClass().add("dark-theme");
        } else {
            scene.getRoot().getStyleClass().add("light-theme");
        }
        
        // Załaduj CSS
        String css = StyleManager.class.getResource(CSS_PATH).toExternalForm();
        if (!scene.getStylesheets().contains(css)) {
            scene.getStylesheets().add(css);
        }
    }

    public static void toggleTheme(Scene scene) {
        Theme newTheme = currentTheme == Theme.LIGHT ? Theme.DARK : Theme.LIGHT;
        applyTheme(scene, newTheme);
    }

    public static Theme getCurrentTheme() {
        return currentTheme;
    }

    public static void applyStyles(Scene scene) {
        applyTheme(scene, currentTheme);
    }
}

