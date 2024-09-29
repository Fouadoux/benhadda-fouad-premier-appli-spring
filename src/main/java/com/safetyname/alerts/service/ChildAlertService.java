package com.safetyname.alerts.service;

import com.safetyname.alerts.dto.ChildResponse;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
public class ChildAlertService implements IChildAlertService {

    private IDataService dataService;
    private static final Logger logger = LogManager.getLogger(ChildAlertService.class);

    @Autowired
    public ChildAlertService(IDataService dataService) {
        this.dataService = dataService;
    }

    public List<ChildResponse> getChildrenByAddress(String address) {
        logger.info("Searching for children at address: {}", address);

        List<Person> persons = dataService.getPersonsByAddress(address);
        if (persons.isEmpty()) {
            logger.warn("No person found at address: {}", address);
            return Collections.emptyList();  // No person found
        }

        List<MedicalRecord> medicalRecords = dataService.getMedicalRecordsByPersons(persons);
        if (medicalRecords.isEmpty()) {
            logger.warn("No medical records found for persons at address: {}", address);
            return Collections.emptyList();
        }


        List<String> family = new ArrayList<>();
        List<ChildResponse> children = new ArrayList<>();


        for (MedicalRecord medicalRecord : medicalRecords) {
            int age = CalculateAgeService.calculateAge(medicalRecord.getBirthdate());
            if (age >= 18) {
                family.add(medicalRecord.getFirstName() + " " + medicalRecord.getLastName());
            } else {
                children.add(new ChildResponse(
                        medicalRecord.getFirstName(),
                        medicalRecord.getLastName(),
                        age,
                        family
                ));
            }
        }

        if (children.isEmpty()) {
            logger.warn("No children found at address: {}", address);
        }

        return children;
    }
}
