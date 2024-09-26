package com.safetyname.alerts.controller;

import com.safetyname.alerts.dto.ChildResponse;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
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
import java.util.stream.Collectors;


/**
 * REST controller to manage alerts related to children at a specific address.
 * <p>
 * This controller provides information about children living at a given address,
 * along with a list of adult family members in the household.
 */
@RestController
@RequestMapping("/childAlert")
public class ChildAlertController {

    private static final Logger logger = LogManager.getLogger(ChildAlertController.class);

    private IChildAlertService childAlertService;

    /**
     * Constructor for ChildAlertController that initializes the data service.
     *
     *   The data service used to access information about persons and medical records.
     */
    public ChildAlertController(IChildAlertService childAlertService) {
        this.childAlertService = childAlertService;
    }

    /**
     * Endpoint to retrieve a list of children living at the specified address, along with the adult members of their household.
     * <p>
     * For each child found at the given address, the response includes:
     * <ul>
     *   <li>First and last name</li>
     *   <li>Age</li>
     *   <li>List of adult family members in the household</li>
     * </ul>
     * If no children are found, a response with HTTP 404 status is returned.
     *
     * @param address The address to check for children and their households.
     * @return ResponseEntity containing a list of {@link ChildResponse} with details about the children and adults.
     * @see ChildResponse
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
