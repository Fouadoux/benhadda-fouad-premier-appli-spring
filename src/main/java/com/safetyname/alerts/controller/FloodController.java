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
 * REST controller for handling flood-related information.
 * <p>
 * This controller provides an endpoint to retrieve information about households covered by specific fire stations.
 * It interacts with the IFloodService to get data on persons and their medical records based on the station numbers.
 * </p>
 */

@RestController
@RequestMapping("/flood")
public class FloodController {

    private static final Logger logger = LogManager.getLogger(FloodController.class);
    private final IFloodService floodService;

    /**
     * Constructor for FloodController that initializes the flood service.
     *
     * @param floodService The service responsible for retrieving flood-related information based on station numbers.
     */

    public FloodController(IFloodService floodService) {
        this.floodService = floodService;
    }

    /**
     * Retrieves households covered by the specified fire stations.
     * <p>
     * This endpoint returns a map where the keys are addresses and the values are lists of persons
     * (with their medical information) residing at those addresses.
     * If no households are found for the given fire stations, a 404 HTTP status is returned.
     * </p>
     *
     * @param stationNumbers A list of fire station numbers to check for covered households.
     * @return ResponseEntity containing a map of addresses and their respective FloodResponse objects or a 404 HTTP status if no households are found.
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
