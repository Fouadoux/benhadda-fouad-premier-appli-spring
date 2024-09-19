package com.safetyname.alerts.controller;

import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.DataService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.safetyname.alerts.utility.Constante.FILEPATH;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for retrieving phone numbers of residents served by a specific fire station.
 * <p>
 * This controller provides an endpoint to get a list of phone numbers for all persons covered by a given fire station number.
 */
@RestController
@RequestMapping("/phoneAlert")
public class PhoneAlertController {

    private static final Logger logger = LogManager.getLogger(PhoneAlertController.class);

    private final DataService dataService;

    /**
     * Constructs a new PhoneAlertController with the given DataService.
     *
     * @param dataService The DataService used to access person and fire station data.
     */
    public PhoneAlertController(DataService dataService) {
        this.dataService = dataService;
        this.dataService.readJsonFile();
    }

    /**
     * Endpoint to retrieve phone numbers of persons covered by the specified fire station number.
     * <p>
     * If no persons are found, returns HTTP 404 Not Found.
     *
     * @param stationNumber The fire station number to search for.
     * @return ResponseEntity containing a list of phone numbers or an error status.
     */
    @GetMapping
    public ResponseEntity<List<String>> getPhoneNumberByFireStation(@RequestParam("firestation") int stationNumber) {
        logger.info("Received request to get phone numbers for fire station number: {}", stationNumber);

        List<Person> persons = dataService.getPersonsByStationNumber(stationNumber);
        if (persons == null || persons.isEmpty()) {
            logger.warn("No persons found for fire station number: {}", stationNumber);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<String> personPhoneNumbers = persons.stream()
                .map(Person::getPhone)
                .collect(Collectors.toList());

        logger.info("Found {} phone number(s) for fire station number: {}", personPhoneNumbers.size(), stationNumber);
        return new ResponseEntity<>(personPhoneNumbers, HttpStatus.OK);
    }
}
