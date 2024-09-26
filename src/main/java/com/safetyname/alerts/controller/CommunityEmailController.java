package com.safetyname.alerts.controller;

import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.CommunityEmailService;
import com.safetyname.alerts.service.DataService;
import com.safetyname.alerts.service.ICommunityEmailService;
import com.safetyname.alerts.service.IDataService;
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
 * This controller provides a way to retrieve a list of emails for residents in a given city.
 */
@RestController
@RequestMapping("/communityEmail")
public class CommunityEmailController {

    private static final Logger logger = LogManager.getLogger(CommunityEmailController.class);

    private ICommunityEmailService communityEmailService;
    /**
     * Constructor for CommunityEmailController that initializes the data service.
     *
     *  The data service used to access information about persons.
     */
    @Autowired
    public CommunityEmailController(ICommunityEmailService communityEmailService) {
        this.communityEmailService = communityEmailService;
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
    public ResponseEntity<List<String>> getCommunityEmailService(@RequestParam("city") String city) {
        List<String> personEmails= communityEmailService.getEmailByCity(city);

        if(personEmails.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(personEmails, HttpStatus.OK);
    }
}
