package com.safetyname.alerts.dto;

import com.safetyname.alerts.entity.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Data Transfer Object representing the response from a fire station request.
 * <p>
 * This class encapsulates information about the persons covered by a specific fire station,
 * including the list of persons, the count of adults, and the count of children.
 * <strong>Note:</strong> This class uses Lombok annotations to automatically generate
 * getters, setters and an all-arguments constructor.
 * Make sure Lombok is properly configured in your development environment.
 */

@Getter
@Setter
@AllArgsConstructor
public class FirestationResponse {
    private List<PersonInfo> persons;
    private long adultCount;
    private long childCount;

}
