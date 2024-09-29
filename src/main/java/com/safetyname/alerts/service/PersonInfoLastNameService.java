package com.safetyname.alerts.service;

import com.safetyname.alerts.controller.PersonInfoLastNameController;
import com.safetyname.alerts.dto.PersonInfoLastNameResponse;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class responsible for retrieving person information based on the last name.
 * <p>
 * This service retrieves personal information, including address, age, email, medications,
 * and allergies for individuals with a given last name.
 */
@Service
public class PersonInfoLastNameService implements IPersonInfoLastNameService {

    private static final Logger logger = LogManager.getLogger(PersonInfoLastNameController.class);
    private final IDataService dataService;

    /**
     * Constructor for PersonInfoLastNameService.
     *
     * @param dataService The data service used to access information about persons and medical records.
     */
    @Autowired
    public PersonInfoLastNameService(IDataService dataService) {
        this.dataService = dataService;
    }

    /**
     * Retrieves detailed information about persons with a given last name.
     * <p>
     * This method fetches the list of persons with the specified last name and their associated
     * medical records. It returns a list of {@link PersonInfoLastNameResponse} objects containing
     * the address, age, email, medications, and allergies of each person.
     *
     * @param lastName The last name to search for.
     * @return A list of {@link PersonInfoLastNameResponse} containing the information about the persons,
     *         or an empty list if no persons or medical records are found.
     */
    public List<PersonInfoLastNameResponse> getPersonInfoLastNameService(String lastName) {
        logger.info("Received request to get person information for last name: {}", lastName);

        if (lastName == null || lastName.trim().isEmpty()) {
            logger.error("Bad request in getPersonInfolastName - Last name is null or empty");
            return Collections.emptyList();
        }

        List<Person> persons = dataService.getPersonsByLastName(lastName);
        if (persons.isEmpty()) {
            logger.warn("No persons found for last name: {}", lastName);
            return Collections.emptyList();
        }

        List<MedicalRecord> medicalRecords = dataService.getMedicalRecordsByPersons(persons);
        if (medicalRecords.isEmpty()) {
            logger.warn("No medical records found for persons with last name: {}", lastName);
            return Collections.emptyList();  // Return empty list if no medical records found
        }

        // Create a map for medical records using the full name as the key
        Map<String, MedicalRecord> medicalRecordMap = medicalRecords.stream()
                .collect(Collectors.toMap(
                        record -> record.getFirstName() + " " + record.getLastName(),
                        record -> record
                ));

        // Build the response by matching persons with their medical records
        List<PersonInfoLastNameResponse> responses = persons.stream()
                .map(person -> {
                    String fullName = person.getFirstName() + " " + person.getLastName();
                    MedicalRecord record = medicalRecordMap.get(fullName);
                    if (record != null) {
                        return new PersonInfoLastNameResponse(
                                person.getLastName(),
                                person.getAddress(),
                                CalculateAgeService.calculateAge(record.getBirthdate()),
                                person.getEmail(),
                                record.getMedications(),
                                record.getAllergies()
                        );
                    }
                    return null;
                })
                .filter(response -> response != null)  // Filter out persons without medical records
                .collect(Collectors.toList());

        logger.info("Found {} person(s) with last name: {}", responses.size(), lastName);
        return responses;
    }
}
