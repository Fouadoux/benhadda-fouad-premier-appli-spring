package com.safetyname.alerts.service;

import com.safetyname.alerts.controller.FireController;
import com.safetyname.alerts.dto.FireInfo;
import com.safetyname.alerts.dto.FireResponse;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FireService implements IFireService {

    private static final Logger logger = LogManager.getLogger(FireController.class);
    private  IDataService dataService;


    @Autowired
    public FireService(IDataService dataService) {
        this.dataService = dataService;
    }



    public FireResponse  getFireService( String address){
        logger.info("Request received for address: {}", address);

        // Retrieve persons living at the specified address
        List<Person> persons = dataService.getPersonsByAddress(address);

        // Retrieve medical records for the persons
        List<MedicalRecord> medicalRecords = dataService.getMedicalRecordsByPersons(persons);

        // Retrieve the fire station serving the address
        int station = dataService.getStationByAddress(address);

        // Build the information related to persons and their health status
        List<FireInfo> fireInfos = persons.stream()
                .flatMap(person -> medicalRecords.stream()
                        .filter(record -> record.getFirstName().equals(person.getFirstName()) &&
                                record.getLastName().equals(person.getLastName()))
                        .map(record -> new FireInfo(
                                person.getLastName(),
                                person.getPhone(),
                                CalculateAgeService.calculateAge(record.getBirthdate()),
                                record.getMedications(),
                                record.getAllergies()
                        )))
                .collect(Collectors.toList());

        // Create the FireResponse containing the information and fire station

        return new FireResponse(fireInfos, station);
    }
}
