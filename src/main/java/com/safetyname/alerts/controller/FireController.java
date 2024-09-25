package com.safetyname.alerts.controller;

import com.safetyname.alerts.dto.FireInfo;
import com.safetyname.alerts.dto.FireResponse;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.CalculateAgeService;
import com.safetyname.alerts.service.DataService;
import com.safetyname.alerts.service.IDataService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


/**
 * REST controller for managing fire alerts at a specific address.
 * <p>
 * This controller provides information about the persons living at a given address,
 * as well as details about their health status (medications, allergies) and the fire station serving that address.
 */
@RestController
@RequestMapping("/fire")
public class FireController {

    private static final Logger logger = LogManager.getLogger(FireController.class);
    private final IDataService dataService;

    /**
     * Constructor for FireController that initializes the data service.
     *
     * @param dataService The data service used to access information about persons, medical records, and fire stations.
     */
    public FireController(IDataService dataService) {
        this.dataService = dataService;
    }

    /**
     * Endpoint to retrieve information about the households living at the specified address, along with the fire station serving that address.
     * <p>
     * For each person found at the given address, the response includes:
     * <ul>
     *   <li>First and last name</li>
     *   <li>Age (calculated from the birthdate)</li>
     *   <li>Prescribed medications</li>
     *   <li>Allergies</li>
     * </ul>
     * If no person is found, a response with HTTP 404 status is returned.
     *
     * @param address The address to check for information about persons and the fire station.
     * @return ResponseEntity containing a {@link FireResponse} object with details about the persons and fire station.
     * @see FireResponse
     */
    @GetMapping
    public ResponseEntity<FireResponse> getFireInfo(@RequestParam("address") String address) {
        logger.info("Request received for address: {}", address);

        // Retrieve persons living at the specified address
        List<Person> persons = dataService.getPersonsByAddress(address);

        // Retrieve medical records for the persons
        List<MedicalRecord> medicalRecords = dataService.getMedicalRecordsByPersons(persons);

        // Retrieve the fire station serving the address
        int station = dataService.getStationByAddress(address);

        // Build the information related to persons and their health status
        List<FireInfo> fireInfos = persons.stream()
                .flatMap(person -> medicalRecords.stream()
                        .filter(record -> record.getFirstName().equals(person.getFirstName()) &&
                                record.getLastName().equals(person.getLastName()))
                        .map(record -> new FireInfo(
                                person.getLastName(),
                                person.getAddress(),
                                CalculateAgeService.calculateAge(record.getBirthdate()),
                                record.getMedications(),
                                record.getAllergies()
                        )))
                .collect(Collectors.toList());

        // Create the FireResponse containing the information and fire station
        FireResponse response = new FireResponse(fireInfos, station);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
