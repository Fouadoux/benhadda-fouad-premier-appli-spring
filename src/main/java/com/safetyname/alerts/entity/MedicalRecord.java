package com.safetyname.alerts.entity;


import lombok.*;

import java.util.List;

/**
 * Entity representing a medical record of a person.
 * <p>
 * This class encapsulates medical information about a person,
 * including their first name, last name, birthdate, medications, and allergies.
 * <p>
 * <strong>Note:</strong> This class uses Lombok annotations to automatically generate
 * getters, setters, constructors, and `equals`/`hashCode` methods.
 * Ensure that Lombok is properly configured in your development environment.
 */

@Getter
@Setter

@NoArgsConstructor
@EqualsAndHashCode
public class MedicalRecord {
    private String firstName;
    private String lastName;
    private String birthdate;
    private List<String> medications;
    private List<String> allergies;

    public MedicalRecord(String firstName, String lastName, String birthdate, List<String> medications, List<String> allergies) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.medications = medications;
        this.allergies = allergies;
    }
}
