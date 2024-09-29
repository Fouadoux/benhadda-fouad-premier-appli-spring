package com.safetyname.alerts.controller;

import com.safetyname.alerts.dto.PersonInfoLastNameResponse;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.CalculateAgeService;
import com.safetyname.alerts.service.DataService;
import com.safetyname.alerts.service.IDataService;
import com.safetyname.alerts.service.IPersonInfoLastNameService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * REST controller for retrieving person information based on last name.
 * <p>
 * This controller provides an endpoint to get detailed information about persons with a given last name,
 * including their address, age, email, medications, and allergies. It interacts with the IPersonInfoLastNameService
 * to fetch the necessary data.
 * </p>
 */

@RestController
@RequestMapping("/personInfolastName")
public class PersonInfoLastNameController {

    private static final Logger logger = LogManager.getLogger(PersonInfoLastNameController.class);

    private final IPersonInfoLastNameService personInfoLastNameService;

    /**
     * Constructor for PersonInfoLastNameController that initializes the person info service.
     *
     * @param personInfoLastNameService The service responsible for retrieving information about persons by last name.
     */

    @Autowired
    public PersonInfoLastNameController(IPersonInfoLastNameService personInfoLastNameService) {
        this.personInfoLastNameService = personInfoLastNameService;
    }

    /**
     * Retrieves detailed information about persons with the specified last name.
     * <p>
     * This endpoint returns a list of {@link PersonInfoLastNameResponse} containing details about persons with the given last name,
     * such as their address, age, email, medications, and allergies. If no persons are found with the given last name, a 404 HTTP status is returned.
     * If the last name is null or empty, a 400 HTTP status (Bad Request) is returned.
     * </p>
     *
     * @param lastName The last name to search for.
     * @return ResponseEntity containing a list of PersonInfoLastNameResponse objects or an error status (400 or 404).
     */

    @GetMapping("/{lastName}")
    public ResponseEntity<List<PersonInfoLastNameResponse>> getPersonInfolastName(@PathVariable String lastName) {
        logger.info("Received request to get person information for last name: {}", lastName);

        if (lastName == null || lastName.trim().isEmpty()) {
            logger.error("Bad request in getPersonInfolastName - Last name is null or empty");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<PersonInfoLastNameResponse> responses=personInfoLastNameService.getPersonInfoLastNameService(lastName);

        if (responses.isEmpty()) {
            logger.warn("No matching records found between persons and medical records for last name: {}", lastName);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        logger.info("Found {} person(s) with last name: {}", responses.size(), lastName);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}
