package com.safetyname.alerts.service;

import com.safetyname.alerts.dto.ChildResponse;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChildAlertService implements IChildAlertService {

    private IDataService dataService;
    private static final Logger logger = LogManager.getLogger(ChildAlertService.class);

    @Autowired
    public ChildAlertService (IDataService dataService){
        this.dataService=dataService;
    }
    public List<ChildResponse> getChildrenByAddress(String address) {
        logger.info("Searching for children at address: {}", address);

        List<Person> persons = dataService.getPersonsByAddress(address);
        if (persons.isEmpty()) {
            logger.warn("No person found at address: {}", address);
            return Collections.emptyList();  // No person found
        }

        List<MedicalRecord> medicalRecords = dataService.getMedicalRecordsByPersons(persons);

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

        return children;
    }
}
