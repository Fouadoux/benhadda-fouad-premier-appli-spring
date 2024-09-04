package com.safetyname.alerts.controller;



import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.DataService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.safetyname.alerts.utility.Constante.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    private static final Logger logger = LogManager.getLogger(PersonController.class);

    private final DataService dataService;


    public PersonController(DataService dataService) throws IOException {
        this.dataService = dataService;
        this.dataService.readJsonFile(FILEPATH);
    }

    // Ajouter une nouvelle personne
    @PostMapping
    public ResponseEntity<String> addPerson(@RequestBody Person newPerson) {
        if (newPerson.getFirstName().isEmpty() ||
                newPerson.getLastName().isEmpty()||newPerson.getAddress().isEmpty()
                ||newPerson.getCity().isEmpty()||newPerson.getEmail().isEmpty()
                ||newPerson.getPhone().isEmpty()|| newPerson.getZip()==0) {
            logger.error("Bad request from addPerson - Missing required fields: {}", newPerson);
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
        try {
            List<Person> persons = dataService.getPersons();
            for(Person person:persons){
                if (person.equals(newPerson)){
                    logger.error("Conflict in addPerson - Existing person: {}", person);
                    return new ResponseEntity<>("Conflit", HttpStatus.CONFLICT);
                }
            }
            persons.add(newPerson);
            dataService.saveData(FILEPATH);
            logger.info("Person added successfully");
            return new ResponseEntity<>("Person added successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to add person",e);
            return new ResponseEntity<>("Failed to add person: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Mettre à jour une personne existante
    @PutMapping
    public ResponseEntity<String> updatePerson(@RequestBody Person updatedPerson) {
        if ((updatedPerson.getFirstName().isEmpty() ||
                updatedPerson.getLastName().isEmpty())) {
            logger.error("Bad request from updatePerson - Missing required fields: {}", updatedPerson);
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
        try {
            List<Person> persons = dataService.getPersons();
            for (Person person : persons) {
                 if (person.getFirstName().equals(updatedPerson.getFirstName()) &&
                        person.getLastName().equals(updatedPerson.getLastName())) {
                    // Mettre à jour les champs
                    person.setAddress(updatedPerson.getAddress());
                    person.setCity(updatedPerson.getCity());
                    person.setZip(updatedPerson.getZip());
                    person.setPhone(updatedPerson.getPhone());
                    person.setEmail(updatedPerson.getEmail());
                    dataService.saveData(FILEPATH);
                     logger.info("Person updated successfully");
                    return new ResponseEntity<>("Person updated successfully", HttpStatus.OK);
                }
            }
            logger.error("Person not found - Existing person: {}, {}", updatedPerson.getFirstName(), updatedPerson.getLastName());
            return new ResponseEntity<>("Person not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Failed to update person",e);
            return new ResponseEntity<>("Failed to update person: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Supprimer une personne
    @DeleteMapping
    public ResponseEntity<String> deletePerson(@RequestParam String firstName, @RequestParam String lastName) {
        try {
            List<Person> persons = dataService.getPersons();
            boolean removed = persons.removeIf(person ->
                    person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)
            );

            if (removed) {
                dataService.saveData(FILEPATH);
                logger.info("Person deleted successfully");
                return new ResponseEntity<>("Person deleted successfully", HttpStatus.OK);
            } else {
                logger.error("Person not found {}, {} ",firstName,lastName);
                return new ResponseEntity<>("Person not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Failed to delete person");
            return new ResponseEntity<>("Failed to delete person: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}