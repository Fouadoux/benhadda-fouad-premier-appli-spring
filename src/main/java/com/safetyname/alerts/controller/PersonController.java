package com.safetyname.alerts.controller;

import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.DataService;
import com.safetyname.alerts.service.IDataService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * REST controller for managing persons.
 * <p>
 * This controller provides endpoints to add, update, and delete person records.
 */
@RestController
@RequestMapping("/person")
public class PersonController {

    private static final Logger logger = LogManager.getLogger(PersonController.class);

    private final IDataService dataService;

    /**
     * Constructor for PersonController that initializes the data service.
     *
     * @param dataService The data service used to access person information.
     * @throws IOException If an error occurs while reading the data file.
     */
    public PersonController(IDataService dataService) throws IOException {
        this.dataService = dataService;
    }

    /**
     * Endpoint to add a new person.
     * <p>
     * If required fields are missing, returns HTTP 400 Bad Request.
     * If the person already exists, returns HTTP 409 Conflict.
     *
     * @param newPerson The new person to add.
     * @return ResponseEntity containing a success message or an error message.
     */
    @PostMapping
    public ResponseEntity<String> addPerson(@RequestBody Person newPerson) {
        logger.info("Received request to add person: {} {}", newPerson.getFirstName(), newPerson.getLastName());

        if (newPerson.getFirstName().trim().isEmpty() ||
                newPerson.getLastName().trim().isEmpty() || newPerson.getAddress().trim().isEmpty()
                || newPerson.getCity().trim().isEmpty() || newPerson.getEmail().trim().isEmpty()
                || newPerson.getPhone().trim().isEmpty() || newPerson.getZip() == 0) {
            logger.error("Bad request in addPerson - Missing required fields. Received: {}", newPerson);
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
        try {
            List<Person> persons = dataService.getPersons();
            for (Person person : persons) {
                if (person.equals(newPerson)) {
                    logger.error("Conflict in addPerson - Person already exists: {} {}", person.getFirstName(), person.getLastName());
                    return new ResponseEntity<>("Conflict in addPerson", HttpStatus.CONFLICT);
                }
            }
            persons.add(newPerson);
            dataService.saveData();
            logger.info("Person added successfully: {} {}", newPerson.getFirstName(), newPerson.getLastName());
            return new ResponseEntity<>("Person added successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to add person", e);
            return new ResponseEntity<>("Failed to add person: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to update an existing person.
     * <p>
     * If required fields are missing, returns HTTP 400 Bad Request.
     * If the person is not found, returns HTTP 404 Not Found.
     *
     * @param updatedPerson The person information to update.
     * @return ResponseEntity containing a success message or an error message.
     */
    @PutMapping
    public ResponseEntity<String> updatePerson(@RequestBody Person updatedPerson) {
        logger.info("Received request to update person: {} {}", updatedPerson.getFirstName(), updatedPerson.getLastName());

        if (updatedPerson.getFirstName().trim().isEmpty() ||
                updatedPerson.getLastName().trim().isEmpty()) {
            logger.error("Bad request in updatePerson - Missing required fields.");
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
        try {
            List<Person> persons = dataService.getPersons();
            for (Person person : persons) {
                if (person.equals(updatedPerson)) {
                    logger.error("Conflict in update Person - Person already exists: {} {}", updatedPerson.getFirstName(), updatedPerson.getLastName());
                    return new ResponseEntity<>("Conflict in update Person", HttpStatus.CONFLICT);
                }
                if (person.getFirstName().equals(updatedPerson.getFirstName()) &&
                        person.getLastName().equals(updatedPerson.getLastName())) {
                    person.setAddress(updatedPerson.getAddress());
                    person.setCity(updatedPerson.getCity());
                    person.setZip(updatedPerson.getZip());
                    person.setPhone(updatedPerson.getPhone());
                    person.setEmail(updatedPerson.getEmail());
                    dataService.saveData();
                    logger.info("Person updated successfully: {} {}", person.getFirstName(), person.getLastName());
                    return new ResponseEntity<>("Person updated successfully", HttpStatus.OK);
                }
            }
            logger.warn("Person not found in updatePerson: {} {}", updatedPerson.getFirstName(), updatedPerson.getLastName());
            return new ResponseEntity<>("Person not found in updatePerson", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Failed to update person", e);
            return new ResponseEntity<>("Failed to update person: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to delete a person.
     * <p>
     * If the person is not found, returns HTTP 404 Not Found.
     *
     * @param firstName The first name of the person to delete.
     * @param lastName  The last name of the person to delete.
     * @return ResponseEntity containing a success message or an error message.
     */
    @DeleteMapping
    public ResponseEntity<String> deletePerson(@RequestParam String firstName, @RequestParam String lastName) {
        logger.info("Received request to delete person: {} {}", firstName, lastName);
        if(firstName.trim().isEmpty()||lastName.trim().isEmpty()){
            logger.error("Bad request in deletePerson - Missing required fields.");
            return new ResponseEntity<>("Bad request in deletePerson", HttpStatus.BAD_REQUEST);
        }
        try {
            List<Person> persons = dataService.getPersons();
            boolean removed = persons.removeIf(person ->
                    person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)
            );

            if (removed) {
                dataService.saveData();
                logger.info("Person deleted successfully: {} {}", firstName, lastName);
                return new ResponseEntity<>("Person deleted successfully", HttpStatus.OK);
            } else {
                logger.warn("Person not found in deletePerson: {} {}", firstName, lastName);
                return new ResponseEntity<>("Person not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Failed to delete person", e);
            return new ResponseEntity<>("Failed to delete person: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
