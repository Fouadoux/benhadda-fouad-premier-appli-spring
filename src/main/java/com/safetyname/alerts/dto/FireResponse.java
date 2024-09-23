package com.safetyname.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Data Transfer Object representing the response for fire-related information at a specific address.
 * <p>
 * This class encapsulates a list of {@link FireInfo} objects containing personal details of residents,
 * along with the number of the fire station that serves the address.
 * <strong>Note:</strong> This class uses Lombok annotations to automatically generate
 * getters, setters and an all-arguments constructor.
 * Make sure Lombok is properly configured in your development environment.
 */

@Getter
@Setter
@AllArgsConstructor
public class FireResponse {
   private List<FireInfo> fireInfos;
   private int station;

}
