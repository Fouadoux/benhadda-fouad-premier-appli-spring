package com.safetyname.alerts.service;

import com.safetyname.alerts.dto.FirestationResponse;
import com.safetyname.alerts.dto.PersonInfo;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class responsible for handling fire station related operations.
 * <p>
 * This service is responsible for retrieving information about people covered
 * by a specific fire station and calculating the number of adults and children.
 */
@Service
public class FireStationService implements IFireStationService {

    private final IDataService dataService;

    /**
     * Constructor for FireStationService.
     *
     * @param dataService The data service used to access information about persons and medical records.
     */
    @Autowired
    public FireStationService(IDataService dataService) {
        this.dataService = dataService;
    }

    /**
     * Retrieves information about persons covered by a specific fire station.
     * <p>
     * This method gathers details about persons covered by the fire station,
     * calculates the number of adults and children, and returns the results in a {@link FirestationResponse}.
     *
     * @param stationNumber The fire station number for which to retrieve the information.
     * @return A {@link FirestationResponse} containing the list of persons, adult count, and child count,
     *         or null if no persons or medical records are found.
     */
    public FirestationResponse getFireStationService(int stationNumber) {
        List<Person> personsCovered = dataService.getPersonsByStationNumber(stationNumber);
        if (personsCovered == null || personsCovered.isEmpty()) {
            return null; // A gérer dans le contrôleur
        }

        List<MedicalRecord> medicalRecords = dataService.getMedicalRecordsByPersons(personsCovered);
        if (medicalRecords == null || medicalRecords.isEmpty()) {
            return null; // A gérer dans le contrôleur
        }

        long adultCount = medicalRecords.stream()
                .filter(medicalRecord -> CalculateAgeService.calculateAge(medicalRecord.getBirthdate()) >= 18)
                .count();

        long childCount = medicalRecords.stream()
                .filter(medicalRecord -> CalculateAgeService.calculateAge(medicalRecord.getBirthdate()) < 18)
                .count();

        List<PersonInfo> personInfoList = personsCovered.stream()
                .map(person -> new PersonInfo(person.getFirstName(), person.getLastName(), person.getAddress(), person.getPhone()))
                .collect(Collectors.toList());

        return new FirestationResponse(personInfoList, adultCount, childCount);
    }
}

