package com.safetyname.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Data Transfer Object representing a child and their family information.
 * <p>
 * This class is used to encapsulate the information about a child, including their first name,
 * last name, age, and a list of family members.
 * <strong>Note:</strong> This class uses Lombok annotations to automatically generate
 * getters, setters and an all-arguments constructor.
 * Make sure Lombok is properly configured in your development environment.
 */

@Getter
@Setter
@AllArgsConstructor
public class ChildResponse {
    private String firstName;
    private String LastName;
    private int age;
    private List<String> family;


}
