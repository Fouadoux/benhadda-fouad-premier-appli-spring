package com.safetyname.alerts.controller;

import com.safetyname.alerts.dto.ChildResponse;

import com.safetyname.alerts.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for handling child alert requests.
 * <p>
 * This controller provides an endpoint to retrieve a list of children and their family members
 * based on the specified address. It interacts with the IChildAlertService to obtain the necessary data.
 * </p>
 */

@RestController
@RequestMapping("/childAlert")
public class ChildAlertController {

    private static final Logger logger = LogManager.getLogger(ChildAlertController.class);

    private IChildAlertService childAlertService;

    /**
     * Constructor for ChildAlertController that initializes the child alert service.
     *
     * @param childAlertService The service responsible for retrieving child alert information.
     */

    public ChildAlertController(IChildAlertService childAlertService) {
        this.childAlertService = childAlertService;
    }

    /**
     * Retrieves a list of children and their family members for the specified address.
     * <p>
     * This endpoint returns a list of children living at the provided address, along with their family members.
     * If no children are found at the address, a 404 HTTP status is returned.
     * </p>
     *
     * @param address The address to check for children.
     * @return ResponseEntity containing a list of ChildResponse objects or a 404 HTTP status if no children are found.
     */

    @GetMapping
    public ResponseEntity<List<ChildResponse>> getChildAlert(@RequestParam("address") String address) {
        List<ChildResponse> children = childAlertService.getChildrenByAddress(address);

        if (children.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(children, HttpStatus.OK);
    }

}
