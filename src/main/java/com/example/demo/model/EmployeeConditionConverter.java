package com.example.demo.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Konwerter dla EmployeeCondition - mapuje wartości z bazy (małe litery) na enum (wielkie litery)
 */
@Converter(autoApply = true)
public class EmployeeConditionConverter implements AttributeConverter<EmployeeCondition, String> {

    @Override
    public String convertToDatabaseColumn(EmployeeCondition condition) {
        if (condition == null) {
            return null;
        }
        // Używamy toString() który zwraca małe litery
        return condition.toString();
    }

    @Override
    public EmployeeCondition convertToEntityAttribute(String dbValue) {
        if (dbValue == null) {
            return null;
        }
        
        // Mapujemy wartości z bazy (małe litery) na stałe enum (wielkie litery)
        switch (dbValue.toLowerCase()) {
            case "obecny":
                return EmployeeCondition.OBECNY;
            case "delegacja":
                return EmployeeCondition.DELEGACJA;
            case "chory":
                return EmployeeCondition.CHORY;
            case "nieobecny":
                return EmployeeCondition.NIEOBECNY;
            default:
                throw new IllegalArgumentException("Unknown condition: " + dbValue);
        }
    }
}

