package com.safetyname.alerts.controller;

import com.safetyname.alerts.entity.Person;
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
 * REST controller for managing email addresses of residents in a specific city.
 * <p>
 * This controller provides a way to retrieve a list of emails for residents in a given city.
 */
@RestController
@RequestMapping("/communityEmail")
public class CommunityEmailController {

    private static final Logger logger = LogManager.getLogger(CommunityEmailController.class);
    private final IDataService dataService;

    /**
     * Constructor for CommunityEmailController that initializes the data service.
     *
     * @param dataService The data service used to access information about persons.
     */
    public CommunityEmailController(IDataService dataService) {
        this.dataService = dataService;
    }

    /**
     * Endpoint to retrieve the list of email addresses for residents of the specified city.
     * <p>
     * If no person is found in the specified city, a 404 HTTP status is returned.
     * If the request is invalid (empty or null city), a 400 HTTP status is returned.
     *
     * @param city The name of the city to retrieve email addresses for.
     * @return ResponseEntity containing a list of strings representing residents' emails, or an HTTP error code.
     */
    @GetMapping
    public ResponseEntity<List<String>> getEmailByCity(@RequestParam("city") String city) {
        logger.info("Request received for city: {}", city);

        if (city == null || city.trim().isEmpty()) {
            logger.warn("Invalid request: city not specified");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // 400 Bad Request if the city is not specified
        }

        List<Person> persons = dataService.getPersons();
        if (persons.isEmpty()) {
            logger.warn("No person found for city: {}", city);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 404 Not Found if no person is found
        }

        // Filter persons from the city and collect their emails
        List<String> personEmails = persons.stream()
                .filter(person -> person.getCity().equalsIgnoreCase(city))
                .map(Person::getEmail)
                .collect(Collectors.toList());

        if (personEmails.isEmpty()) {
            logger.warn("No emails found for city: {}", city);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 404 Not Found if no emails are found
        }

        return new ResponseEntity<>(personEmails, HttpStatus.OK);
    }
}
