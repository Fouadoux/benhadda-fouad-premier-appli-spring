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



@RestController
@RequestMapping("/communityEmail")
public class CommunityEmailController {

    private static final Logger logger = LogManager.getLogger(CommunityEmailController.class);

    private ICommunityEmailService communityEmailService;

    @Autowired
    public CommunityEmailController(ICommunityEmailService communityEmailService) {
        this.communityEmailService = communityEmailService;
    }


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
