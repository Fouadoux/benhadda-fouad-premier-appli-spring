package com.safetyname.alerts.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Data Transfer Object representing the response for households in a flood situation.
 * <p>
 * This class encapsulates information about individuals living in households served by specific fire stations,
 * including their first name, last name, phone number, age, medications, and allergies.
 * <strong>Note:</strong> This class uses Lombok annotations to automatically generate
 * getters, setters.
 * Make sure Lombok is properly configured in your development environment.
 */

@Getter
@Setter
public class FloodResponse {
    private String firstName;
    private String lastName;
    private String phone;
    private int age;
    private List<String> medications;
    private List<String> allergies;

    public FloodResponse(String firstName, String lastName, String phone, int age, List<String> medications, List<String> allergies) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.age = age;
        this.medications = medications;
        this.allergies = allergies;
    }

}
