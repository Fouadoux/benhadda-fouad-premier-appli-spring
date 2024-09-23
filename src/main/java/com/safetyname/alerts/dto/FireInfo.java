package com.safetyname.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Data Transfer Object representing fire-related information for a person.
 * <p>
 * This class encapsulates information about a person relevant in a fire incident,
 * including their last name, phone number, age, medications, and allergies.
 * <strong>Note:</strong> This class uses Lombok annotations to automatically generate
 * getters, setters and an all-arguments constructor.
 * Make sure Lombok is properly configured in your development environment.
 */

@Getter
@Setter
@AllArgsConstructor
public class FireInfo {
    private String lastName;
    private String phone;
    private int age;
    private List<String> medications;
    private List<String> allergies;
}
