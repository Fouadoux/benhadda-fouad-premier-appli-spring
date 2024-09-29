package com.safetyname.alerts.controller;

import com.safetyname.alerts.dto.FirestationResponse;
import com.safetyname.alerts.entity.FireStation;
import com.safetyname.alerts.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing fire stations.
 * <p>
 * This controller allows the creation, update, deletion, and retrieval of information about fire stations
 * and people covered by a specific fire station.
 * It handles HTTP requests for fire station data and interacts with services to manage data.
 * </p>
 */

@RestController
@RequestMapping("/firestation")
public class FirestationController {

    private static final Logger logger = LogManager.getLogger(FirestationController.class);

    private final IFireStationService fireStationService;
    private final IDataService dataService;

    /**
     * Constructor for FirestationController that initializes the necessary services.
     *
     * @param fireStationService The service used for fire station related operations.
     * @param dataService The data service used for accessing fire station and person data.
     */

    @Autowired
    public FirestationController(IFireStationService fireStationService,IDataService dataService) {
        this.fireStationService = fireStationService;
        this.dataService=dataService;
    }

    /**
     * Adds a new fire station.
     * <p>
     * This endpoint accepts a FireStation object in the request body and adds it to the system.
     * If the fire station already exists or if the request is malformed, appropriate error codes are returned.
     * </p>
     *
     * @param newFireStation The FireStation object containing address and station number to be added.
     * @return A ResponseEntity with the result of the operation: 201 Created, 400 Bad Request, 409 Conflict, or 500 Internal Server Error.
     */

    @PostMapping
    public ResponseEntity<String> addFirestation(@RequestBody FireStation newFireStation) {
        if (newFireStation.getAddress().trim().isEmpty() || newFireStation.getStation() == 0) {
            logger.error("bad request - Missing required fields");
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
        try {
            List<FireStation> fireStations = dataService.getFireStations();
            for (FireStation fireStation : fireStations) {
                if (fireStation.getAddress().equals(newFireStation.getAddress())) {
                    logger.error("Conflict in addFirestation - Existing firestation: {}", newFireStation.getAddress());
                    return new ResponseEntity<>("Conflict", HttpStatus.CONFLICT);
                }
            }
            fireStations.add(newFireStation);
            dataService.saveData();
            return new ResponseEntity<>("Fire Station added successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to add fire station", e);
            return new ResponseEntity<>("Failed to add fire station: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates an existing fire station.
     * <p>
     * This endpoint updates the fire station details for the specified address. If the fire station doesn't exist,
     * or if the request is malformed, appropriate error codes are returned.
     * </p>
     *
     * @param updateFirestation The FireStation object containing the updated details.
     * @return A ResponseEntity with the result of the operation: 200 OK, 400 Bad Request, 404 Not Found, 409 Conflict, or 500 Internal Server Error.
     */

    @PutMapping
    public ResponseEntity<String> updateFirestation(@RequestBody FireStation updateFirestation) {
        if (updateFirestation.getAddress().trim().isEmpty() || updateFirestation.getStation() == 0) {
            logger.error("Bad request from update fire station - Missing required fields: ({}) ({}) ", updateFirestation.getAddress(), updateFirestation.getStation());
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
        try {
            List<FireStation> fireStations = dataService.getFireStations();
            for (FireStation fireStation : fireStations) {
                if (fireStation.getAddress().equals(updateFirestation.getAddress())) {

                    if (fireStation.getStation() == updateFirestation.getStation()) {
                        logger.error("Conflict in update fire station - Fire station already exists with the same address and station: {}, {}", updateFirestation.getAddress(), updateFirestation.getStation());
                        return new ResponseEntity<>("Conflict: Fire station already exists with the same address and station", HttpStatus.CONFLICT);
                    }

                    fireStation.setStation(updateFirestation.getStation());
                    dataService.saveData();
                    logger.info("Fire station updated successfully");
                    return new ResponseEntity<>("Fire station updated successfully", HttpStatus.OK);
                }
            }
            logger.error("Fire station address not found: {}, {}", updateFirestation.getAddress(), updateFirestation.getStation());
            return new ResponseEntity<>("Fire station address not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Failed to update fire station", e);
            return new ResponseEntity<>("Failed to update fire station: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a fire station by its address.
     * <p>
     * This endpoint deletes the fire station associated with the provided address.
     * If the fire station is not found, a 404 Not Found response is returned.
     * </p>
     *
     * @param address The address of the fire station to be deleted.
     * @return A ResponseEntity with the result of the operation: 200 OK, 400 Bad Request, 404 Not Found, or 500 Internal Server Error.
     */

    @DeleteMapping
    public ResponseEntity<String> deleteFirestation(@RequestParam String address) {
        if (address.trim().isEmpty()) {
            logger.error("Bad request from delete fire station");
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
        try {
            List<FireStation> fireStations = dataService.getFireStations();
            boolean removed = fireStations.removeIf(fireStation -> fireStation.getAddress().equals(address));
            if (removed) {
                dataService.saveData();
                logger.info("Fire station deleted successfully");
                return new ResponseEntity<>("Fire station deleted successfully", HttpStatus.OK);
            } else {
                logger.error("Address not found: {}", address);
                return new ResponseEntity<>("Address not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Failed to delete fire station", e);
            return new ResponseEntity<>("Failed to delete fire station: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves information about people covered by a specific fire station.
     * <p>
     * This endpoint returns a list of people covered by the fire station, including counts of adults and children.
     * If no data is found, a 404 Not Found response is returned.
     * </p>
     *
     * @param stationNumber The fire station number to check for coverage.
     * @return A ResponseEntity containing a FirestationResponse object with the list of people and counts, or 404 Not Found.
     */


    @GetMapping
    public ResponseEntity<FirestationResponse> getPersonsCoveredByFirestation(@RequestParam("stationNumber") int stationNumber) {
        logger.info("Request received for fire station number: {}", stationNumber);

        FirestationResponse response = fireStationService.getFireStationService(stationNumber);
        if (response == null) {
            logger.warn("No people or medical records found for fire station number: {}", stationNumber);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        logger.info("Returning information for fire station number: {}", stationNumber);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
