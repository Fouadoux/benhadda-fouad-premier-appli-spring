package com.safetyname.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object representing basic personal information.
 * <p>
 * This class encapsulates basic details about a person, including
 * their first name, last name, address, and phone number.
 * <strong>Note:</strong> This class uses Lombok annotations to automatically generate
 * getters, setters and an all-arguments constructor.
 * Make sure Lombok is properly configured in your development environment.
 */

@Getter
@Setter
@AllArgsConstructor
public class PersonInfo {
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
}
