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
 * including their address, age, email, medications, and allergies.
 */
@RestController
@RequestMapping("/personInfolastName")
public class PersonInfoLastNameController {

    private static final Logger logger = LogManager.getLogger(PersonInfoLastNameController.class);

    private final IPersonInfoLastNameService personInfoLastNameService;


    @Autowired
    public PersonInfoLastNameController(IPersonInfoLastNameService personInfoLastNameService) {
        this.personInfoLastNameService = personInfoLastNameService;
    }


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
