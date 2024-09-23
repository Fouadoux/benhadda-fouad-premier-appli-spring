package com.safetyname.alerts.controller;

import com.safetyname.alerts.dto.PersonInfoLastNameResponse;
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


/**
 * REST controller for retrieving person information based on last name.
 * <p>
 * This controller provides an endpoint to get detailed information about persons with a given last name,
 * including their address, age, email, medications, and allergies.
 */
@RestController
@RequestMapping("/personInfolastName")
public class PersonInfoLastNameController {

    private static final Logger logger = LogManager.getLogger(PersonInfoLastNameController.class);

    private final DataService dataService;

    /**
     * Constructs a new PersonInfoLastNameController with the given DataService.
     *
     * @param dataService The DataService used to access person and medical record data.
     */
    public PersonInfoLastNameController(DataService dataService) {
        this.dataService = dataService;
    }

    /**
     * Endpoint to retrieve detailed information about persons with a specified last name.
     * <p>
     * The response includes the last name, address, age, email, medications, and allergies of each person found.
     * If no persons or medical records are found, appropriate HTTP status codes are returned.
     *
     * @param lastName The last name to search for.
     * @return ResponseEntity containing a list of {@link PersonInfoLastNameResponse} or an error status.
     */
    @GetMapping("/{lastName}")
    public ResponseEntity<List<PersonInfoLastNameResponse>> getPersonInfolastName(@PathVariable String lastName) {
        logger.info("Received request to get person information for last name: {}", lastName);

        if (lastName == null || lastName.trim().isEmpty()) {
            logger.error("Bad request in getPersonInfolastName - Last name is null or empty");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Person> personByLastName = dataService.getPersonsByLastName(lastName);
        if (personByLastName.isEmpty()) {
            logger.warn("No person found for last name: {}", lastName);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 404 Not Found if no person found
        }

        List<MedicalRecord> medicalRecords = dataService.getMedicalRecordsByPersons(personByLastName);
        if (medicalRecords.isEmpty()) {
            logger.warn("No medical records found for persons with last name: {}", lastName);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 404 Not Found if no medical records found
        }

        List<PersonInfoLastNameResponse> responses = personByLastName.stream()
                .flatMap(person -> medicalRecords.stream()
                        .filter(record -> record.getLastName().equals(person.getLastName()) && record.getFirstName().equals(person.getFirstName()))
                        .map(record -> new PersonInfoLastNameResponse(
                                person.getLastName(),
                                person.getAddress(),
                                CalculateAgeService.calculateAge(record.getBirthdate()),
                                person.getEmail(),
                                record.getMedications(),
                                record.getAllergies()
                        )))
                .collect(Collectors.toList());

        if (responses.isEmpty()) {
            logger.warn("No matching records found between persons and medical records for last name: {}", lastName);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        logger.info("Found {} person(s) with last name: {}", responses.size(), lastName);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}
