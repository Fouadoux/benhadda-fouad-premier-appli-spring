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

/**
 * Service class responsible for handling fire-related information requests.
 * <p>
 * This service retrieves information about the residents of a specified address, including
 * their medical records, health details, and the fire station serving the address.
 */
@Service
public class FireService implements IFireService {

    private static final Logger logger = LogManager.getLogger(FireController.class);
    private  IDataService dataService;

    /**
     * Constructor for FireService.
     *
     * @param dataService The data service used to access information about persons, medical records, and fire stations.
     */
    @Autowired
    public FireService(IDataService dataService) {
        this.dataService = dataService;
    }

    /**
     * Retrieves fire-related information for residents at a specified address.
     * <p>
     * This method gathers details about the people living at the given address,
     * including their age, medications, and allergies, along with the fire station serving the address.
     *
     * @param address The address to retrieve fire information for.
     * @return A {@link FireResponse} object containing a list of persons' health information and the fire station number.
     */
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
