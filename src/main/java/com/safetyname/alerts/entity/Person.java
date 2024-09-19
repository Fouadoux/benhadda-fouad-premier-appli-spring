package com.safetyname.alerts.entity;

import lombok.*;

/**
 * Entity representing a person.
 * <p>
 * This class encapsulates personal information about an individual,
 * including their first name, last name, address, city, email, zip code, and phone number.
 * <p>
 * <strong>Note:</strong> This class uses Lombok annotations to automatically generate
 * getters, setters, constructors, and `equals`/`hashCode` methods.
 * Ensure that Lombok is properly configured in your development environment.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Person {
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String email;
    private int zip=0;
    private String phone;

}
