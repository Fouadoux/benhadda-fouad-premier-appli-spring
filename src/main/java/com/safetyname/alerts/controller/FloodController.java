package com.safetyname.alerts.controller;

import com.safetyname.alerts.dto.FloodResponse;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.CalculateAgeService;
import com.safetyname.alerts.service.DataService;
import com.safetyname.alerts.service.IDataService;
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
    private final IDataService dataService;

    /**
     * Constructor for FloodController that initializes the data service.
     *
     * @param dataService The data service used to access information about people, addresses, and fire stations.
     */
    public FloodController(IDataService dataService) {
        this.dataService = dataService;
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
        logger.info("Request received for fire stations: {}", stationNumbers);

        if (stationNumbers == null || stationNumbers.isEmpty()) {
            logger.warn("Empty list of fire station numbers: bad request");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // 1. Retrieve the addresses served by the specified fire stations
        Set<String> addresses = stationNumbers.stream()
                .flatMap(stationNumber -> dataService.getAddressesByStationNumber(stationNumber).stream())
                .collect(Collectors.toSet());

        if (addresses.isEmpty()) {
            logger.warn("No addresses found for fire stations: {}", stationNumbers);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 2. Retrieve the individuals living at these addresses
        List<Person> persons = addresses.stream()
                .flatMap(address -> dataService.getPersonsByAddress(address).stream())
                .collect(Collectors.toList());

        if (persons.isEmpty()) {
            logger.warn("No people found at addresses: {}", addresses);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 3. Retrieve the medical records for these individuals
        List<MedicalRecord> medicalRecords = dataService.getMedicalRecordsByPersons(persons);

        // Create a Map for the medical records using "FirstName LastName" as the key
        Map<String, MedicalRecord> medicalRecordMap = new HashMap<>();
        for (MedicalRecord record : medicalRecords) {
            String key = (record.getFirstName() + " " + record.getLastName()).trim().toLowerCase();
            medicalRecordMap.put(key, record);
        }

        // Create a Map to group households by address
        Map<String, List<FloodResponse>> householdsByAddress = new HashMap<>();

        // Iterate through all individuals
        for (Person person : persons) {
            String address = person.getAddress();
            String key = (person.getFirstName() + " " + person.getLastName()).trim().toLowerCase();

            // Retrieve the corresponding medical record
            MedicalRecord medicalRecord = medicalRecordMap.get(key);

            // Calculate age and retrieve medications and allergies
            int age = 0;
            List<String> medications = Collections.emptyList();
            List<String> allergies = Collections.emptyList();

            if (medicalRecord != null) {
                age = CalculateAgeService.calculateAge(medicalRecord.getBirthdate());
                medications = medicalRecord.getMedications();
                allergies = medicalRecord.getAllergies();
            }

            // Create the FloodResponse for the person
            FloodResponse response = new FloodResponse(person.getFirstName(), person.getLastName(), person.getPhone(), age, medications, allergies);

            // Add the FloodResponse to the corresponding list in the Map
            householdsByAddress.computeIfAbsent(address, k -> new ArrayList<>()).add(response);
        }

        return new ResponseEntity<>(householdsByAddress, HttpStatus.OK);
    }
}
