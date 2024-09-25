package com.safetyname.alerts.controller;

import com.safetyname.alerts.dto.ChildResponse;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.CalculateAgeService;
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
 * REST controller to manage alerts related to children at a specific address.
 * <p>
 * This controller provides information about children living at a given address,
 * along with a list of adult family members in the household.
 */
@RestController
@RequestMapping("/childAlert")
public class ChildAlertController {

    private static final Logger logger = LogManager.getLogger(ChildAlertController.class);

    private IDataService dataService;

    /**
     * Constructor for ChildAlertController that initializes the data service.
     *
     * @param dataService The data service used to access information about persons and medical records.
     */
    public ChildAlertController(IDataService dataService) {
        this.dataService = dataService;
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
        logger.info("Searching for children at address: {}", address);

        List<Person> person = dataService.getPersonsByAddress(address);
        if (person.isEmpty()) {
            logger.warn("No person found at address: {}", address);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // No person found
        }

        List<MedicalRecord> medicalRecords = dataService.getMedicalRecordsByPersons(person);

        // Get the list of adults in the household
        List<String> family = medicalRecords.stream()
                .filter(medicalRecord -> CalculateAgeService.calculateAge(medicalRecord.getBirthdate()) >= 18)
                .map(medicalRecord -> medicalRecord.getFirstName() + " " + medicalRecord.getLastName())
                .collect(Collectors.toList());

        // Get the list of children in the household
        List<ChildResponse> children = medicalRecords.stream()
                .filter(medicalRecord -> CalculateAgeService.calculateAge(medicalRecord.getBirthdate()) < 18)  // Filter minors
                .map(medicalRecord -> new ChildResponse(
                        medicalRecord.getFirstName(),
                        medicalRecord.getLastName(),
                        CalculateAgeService.calculateAge(medicalRecord.getBirthdate()),  // Recalculate age
                        family))
                .collect(Collectors.toList());

        if (children.isEmpty()) {
            logger.warn("No children found at address: {}", address);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        logger.info("Number of children found: {}", children.size());
        return new ResponseEntity<>(children, HttpStatus.OK);
    }

}
