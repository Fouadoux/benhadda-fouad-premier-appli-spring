package com.safetyname.alerts.controller;


import com.safetyname.alerts.service.ICommunityEmailService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
 * This controller provides an endpoint to retrieve a list of email addresses for residents of a specified city.
 * It interacts with the ICommunityEmailService to retrieve the data.
 * </p>
 */

@RestController
@RequestMapping("/communityEmail")
public class CommunityEmailController {

    private static final Logger logger = LogManager.getLogger(CommunityEmailController.class);

    private ICommunityEmailService communityEmailService;

    /**
     * Constructor for CommunityEmailController that initializes the community email service.
     *
     * @param communityEmailService The service responsible for retrieving email addresses for residents in a city.
     */

    @Autowired
    public CommunityEmailController(ICommunityEmailService communityEmailService) {
        this.communityEmailService = communityEmailService;
    }

    /**
     * Retrieves a list of email addresses for residents of the specified city.
     * <p>
     * This endpoint returns a list of email addresses for all the persons living in the given city.
     * If the city parameter is missing or empty, a 400 HTTP status (Bad Request) is returned.
     * If no email addresses are found for the specified city, a 404 HTTP status is returned.
     * </p>
     *
     * @param city The name of the city to retrieve email addresses for.
     * @return ResponseEntity containing a list of email addresses or an error status (400 or 404).
     */

    @GetMapping
    public ResponseEntity<List<String>> getCommunityEmailService(@RequestParam("city") String city) {
        if (city == null || city.trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<String> personEmails= communityEmailService.getEmailByCity(city);

        if(personEmails.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(personEmails, HttpStatus.OK);
    }
}
