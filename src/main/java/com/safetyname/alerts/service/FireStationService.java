package com.safetyname.alerts.service;

import com.safetyname.alerts.dto.FirestationResponse;
import com.safetyname.alerts.dto.PersonInfo;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class FireStationService implements IFireStationService {

    private final IDataService dataService;

    @Autowired
    public FireStationService(IDataService dataService) {
        this.dataService = dataService;
    }

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

