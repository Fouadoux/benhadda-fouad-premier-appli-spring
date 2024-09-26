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

@Service
public class CommunityEmailService implements ICommunityEmailService {

    private static final Logger logger = LogManager.getLogger(CommunityEmailController.class);
    private IDataService dataService;

    @Autowired
    public CommunityEmailService(IDataService dataService){
        this.dataService=dataService;
    }




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
