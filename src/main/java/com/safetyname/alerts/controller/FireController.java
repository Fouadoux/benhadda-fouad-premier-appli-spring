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

/**
 * REST controller for managing fire-related information.
 * <p>
 * This controller provides an endpoint to retrieve information about people living at a specified address
 * and the fire station that serves that address. It interacts with the IFireService to fetch the required data.
 * </p>
 */

@RestController
@RequestMapping("/fire")
public class FireController {

    private static final Logger logger = LogManager.getLogger(FireController.class);
    private  IFireService fireService;

    /**
     * Constructor for FireController that initializes the fire service.
     *
     * @param fireService The service responsible for retrieving fire-related information for a given address.
     */

    public FireController(IFireService fireService) {
        this.fireService =fireService;
    }

    /**
     * Retrieves fire-related information for the specified address.
     * <p>
     * This endpoint returns details about people living at the specified address, including their medical information and the fire station that covers the address.
     * If the address parameter is missing or empty, a 400 HTTP status (Bad Request) is returned.
     * If no data is found for the address, a 404 HTTP status is returned.
     * </p>
     *
     * @param address The address to retrieve fire-related information for.
     * @return ResponseEntity containing a FireResponse object or an error status (400 or 404).
     */

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
