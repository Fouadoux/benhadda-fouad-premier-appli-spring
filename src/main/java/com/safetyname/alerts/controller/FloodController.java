package com.safetyname.alerts.controller;

import com.safetyname.alerts.dto.FloodResponse;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.CalculateAgeService;
import com.safetyname.alerts.service.DataService;
import com.safetyname.alerts.service.IDataService;
import com.safetyname.alerts.service.IFloodService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


/**
 * REST controller for managing flood-related information.
 * <p>
 * This controller allows retrieving information about households served by one or more fire stations,
 * including details about the individuals living at these addresses and their medical records.
 */
@RestController
@RequestMapping("/flood")
public class FloodController {

    private static final Logger logger = LogManager.getLogger(FloodController.class);
    private final IFloodService floodService;

    /**
     * Constructor for FloodController that initializes the data service.
     *
     *  The data service used to access information about people, addresses, and fire stations.
     */
    public FloodController(IFloodService floodService) {
        this.floodService = floodService;
    }

    /**
     * Endpoint to retrieve households served by the specified fire stations.
     * <p>
     * The response groups individuals by address, including their first name, last name, phone number, age, medications, and allergies.
     * If no fire stations are specified or no addresses are found, appropriate error codes are returned.
     *
     * @param stationNumbers List of fire station numbers to retrieve household information for.
     * @return ResponseEntity containing a Map where each key is an address and the value is a list of {@link FloodResponse}.
     * @see FloodResponse
     */
    @GetMapping("/stations")
    public ResponseEntity<Map<String, List<FloodResponse>>> getHouseholdsByStation(@RequestParam("stations") List<Integer> stationNumbers) {

        Map<String, List<FloodResponse>> householdsByAddress = floodService.getFloodService(stationNumbers);

        if (householdsByAddress.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(householdsByAddress, HttpStatus.OK);
    }
}
