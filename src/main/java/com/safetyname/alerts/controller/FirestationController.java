package com.safetyname.alerts.controller;

import com.safetyname.alerts.dto.FirestationResponse;
import com.safetyname.alerts.dto.PersonInfo;
import com.safetyname.alerts.entity.FireStation;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.CalculateAgeService;
import com.safetyname.alerts.service.DataService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.safetyname.alerts.utility.Constante.FILEPATH;

/**
 * REST controller for managing fire stations.
 * <p>
 * This controller allows adding, updating, deleting fire stations,
 * and retrieving information about people covered by a specific fire station.
 */
@RestController
@RequestMapping("/firestation")
public class FirestationController {

    private static final Logger logger = LogManager.getLogger(FirestationController.class);
    private final DataService dataService;

    /**
     * Constructor for FirestationController that initializes the data service.
     *
     * @param dataService The data service used to access information about fire stations and people.
     */
    public FirestationController(DataService dataService) {
        this.dataService = dataService;
        this.dataService.readJsonFile();
    }

    /**
     * Endpoint to add a new fire station.
     * <p>
     * If the address or station number is missing, a 400 HTTP status is returned. If there is a conflict with an existing address, a 409 HTTP status is returned.
     *
     * @param newFireStation The new fire station information to be added.
     * @return ResponseEntity containing a success message or error.
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
     * Endpoint to update an existing fire station.
     * <p>
     * If the address or station number is missing, a 400 HTTP status is returned. If the address is not found, a 404 HTTP status is returned.
     *
     * @param updateFirestation The fire station information to be updated.
     * @return ResponseEntity containing a success message or error.
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
     * Endpoint to delete a fire station by its address.
     * <p>
     * If the address is not found, a 404 HTTP status is returned.
     *
     * @param address The address of the fire station to be deleted.
     * @return ResponseEntity containing a success message or error.
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
     * Endpoint to retrieve information about people covered by a specific fire station.
     * <p>
     * The response includes information about the covered people as well as the count of adults and children.
     * If no people are found, a 404 HTTP status is returned.
     *
     * @param stationNumber The fire station number to check.
     * @return ResponseEntity containing a {@link FirestationResponse} object with information about the covered people.
     * @see FirestationResponse
     */
    @GetMapping
    public ResponseEntity<FirestationResponse> getPersonsCoveredByFirestation(@RequestParam("stationNumber") int stationNumber) {
        logger.info("Request received for fire station number: {}", stationNumber);

        List<Person> personsCovered = dataService.getPersonsByStationNumber(stationNumber);
        if (personsCovered == null || personsCovered.isEmpty()) {
            logger.warn("No people found for fire station number: {}", stationNumber);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<MedicalRecord> medicalRecords = dataService.getMedicalRecordsByPersons(personsCovered);
        if (medicalRecords == null || medicalRecords.isEmpty()) {
            logger.warn("No medical records found for people of fire station number: {}", stationNumber);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        long adultCount = medicalRecords.stream()
                .filter(medicalRecord -> CalculateAgeService.calculateAge(medicalRecord.getBirthdate()) >= 18)
                .count();

        long childCount = medicalRecords.stream()
                .filter(medicalRecord -> CalculateAgeService.calculateAge(medicalRecord.getBirthdate()) < 18)
                .count();

        List<PersonInfo> personInfoList = personsCovered.stream()
                .map(person -> new PersonInfo(person.getFirstName(), person.getLastName(), person.getAddress(), person.getPhone()))
                .collect(Collectors.toList());

        FirestationResponse response = new FirestationResponse(personInfoList, adultCount, childCount);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
