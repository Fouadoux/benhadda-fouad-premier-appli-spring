package com.safetyname.alerts.controller;


import com.safetyname.alerts.dto.FireResponse;
import com.safetyname.alerts.service.IFireService;
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



@RestController
@RequestMapping("/fire")
public class FireController {

    private static final Logger logger = LogManager.getLogger(FireController.class);
    private  IFireService fireService;


    public FireController(IFireService fireService) {
        this.fireService =fireService;
    }


    @GetMapping
    public ResponseEntity<FireResponse> getFireInfo(@RequestParam("address") String address) {
        logger.info("Request received for address: {}", address);

        if (address == null || address.trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        FireResponse response = fireService.getFireService(address);

        if(response==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
