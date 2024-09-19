package com.safetyname.alerts.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

/**
 * Entity representing a fire station.
 * <p>
 * This class encapsulates information about a fire station,
 * including the address it serves and its station number.
 * <p>
 * <strong>Note:</strong> This class uses Lombok annotations to automatically generate
 * getters, setters, an all-arguments constructor, and a no-arguments constructor.
 * Make sure Lombok is properly configured in your development environment.
 */

public class FireStation {
    private String address;
    private int station;
}
