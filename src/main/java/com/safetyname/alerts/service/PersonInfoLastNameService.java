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

@Service
public class PersonInfoLastNameService implements  IPersonInfoLastNameService{

    private static final Logger logger = LogManager.getLogger(PersonInfoLastNameController.class);
    private final IDataService dataService;

    @Autowired
    public PersonInfoLastNameService(IDataService dataService){
        this.dataService=dataService;
    }


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
            return Collections.emptyList();  // 404 Not Found if no medical records found
        }


        Map<String, MedicalRecord> medicalRecordMap = medicalRecords.stream()
                .collect(Collectors.toMap(
                        record -> record.getFirstName() + " " + record.getLastName(),
                        record -> record
                ));

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
                .filter(response -> response != null)  // Filtre les personnes sans dossier médical
                .collect(Collectors.toList());

        logger.info("Found {} person(s) with last name: {}", responses.size(), lastName);
        return responses;
    }
}
