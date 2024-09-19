package com.safetyname.alerts.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * Data Transfer Object representing detailed personal information based on last name.
 * <p>
 * This class encapsulates information about a person, including
 * their last name, address, age, email, medications, and allergies.
 * <strong>Note:</strong> This class uses Lombok annotations to automatically generate
 * getters, setters.
 * Make sure Lombok is properly configured in your development environment.
 */

@Getter
@Setter
public class PersonInfoLastNameResponse {

    private String lastName;
    private String address;
    private int age;
    private String email;
    private List<String > medications;
    private List<String > allergies;

    public PersonInfoLastNameResponse(String lastName , String address, int age, String email, List<String> allergies, List<String> medications) {

        this.allergies = allergies;
        this.medications = medications;
        this.email = email;
        this.age = age;
        this.address = address;
        this.lastName = lastName;
    }

}
