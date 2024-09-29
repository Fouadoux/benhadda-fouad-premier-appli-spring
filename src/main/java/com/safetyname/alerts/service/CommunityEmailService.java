package com.safetyname.alerts.service;

import com.safetyname.alerts.controller.CommunityEmailController;
import com.safetyname.alerts.entity.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Service responsible for retrieving email addresses of persons living in a specific city.
 * <p>
 * This service interacts with the data layer to fetch persons' information and filter by city,
 * returning a list of email addresses of residents.
 */
@Service
public class CommunityEmailService implements ICommunityEmailService {

    private static final Logger logger = LogManager.getLogger(CommunityEmailController.class);
    private final IDataService dataService;

    /**
     * Constructor for CommunityEmailService.
     *
     * @param dataService The data service used to access information about persons.
     */
    @Autowired
    public CommunityEmailService(IDataService dataService){
        this.dataService=dataService;
    }

    /**
     * Retrieves the email addresses of all persons living in the specified city.
     * <p>
     * This method fetches all persons, filters them based on the city name (case-insensitive),
     * and returns their email addresses. If no persons or emails are found, an empty list is returned.
     *
     * @param city The name of the city to retrieve email addresses for.
     * @return A list of email addresses of persons living in the specified city, or an empty list if no persons are found.
     */
   public List<String> getEmailByCity (String city) {
        logger.info("Request received for city: {}", city);

        List<Person> persons = dataService.getPersons();
        if (persons.isEmpty()) {
            logger.warn("No person found for city: {}", city);
            return  Collections.emptyList();  // 404 Not Found if no person is found
        }

        // Filter persons from the city and collect their emails
        List<String> personEmails = persons.stream()
                .filter(person -> person.getCity().equalsIgnoreCase(city))
                .map(Person::getEmail)
                .collect(Collectors.toList());

        if (personEmails.isEmpty()) {
            logger.warn("No emails found for city: {}", city);
            return Collections.emptyList();  // 404 Not Found if no emails are found
        }

        return personEmails;
    }



}
