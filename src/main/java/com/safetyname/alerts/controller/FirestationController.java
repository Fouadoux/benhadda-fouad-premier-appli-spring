package com.safetyname.alerts.controller;


import com.safetyname.alerts.dto.FirestationResponse;
import com.safetyname.alerts.dto.PersonInfo;
import com.safetyname.alerts.entity.FireStation;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.CalculateAgeService;
import com.safetyname.alerts.service.DataService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.safetyname.alerts.utility.Constante.FILEPATH;

@RestController
@RequestMapping("/firestation")
public class FirestationController {

    private static final Logger logger = LogManager.getLogger(FirestationController.class);

    private DataService dataService;

    public FirestationController(DataService dataService) throws IOException {
        this.dataService = dataService;
        this.dataService.readJsonFile(FILEPATH);
    }

    //Ajouter une nouvelle firestation
    @PostMapping
    public ResponseEntity<String> addFirestation(@RequestBody FireStation newFireStation) {
        if (newFireStation.getAddress().isEmpty() || newFireStation.getStation() == 0) {
            logger.error("bad request - Missing requied fileds");
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
        try {
            List<FireStation> fireStations = dataService.getFireStations();
            for (FireStation fireStation : fireStations) {
                if (fireStation.getAddress().equals(newFireStation.getAddress())) {
                    logger.error("Conflict in addPerson - Existing person: {}", newFireStation.getAddress());
                    return new ResponseEntity<>("Conflit", HttpStatus.CONFLICT);
                }
            }
            fireStations.add(newFireStation);
            dataService.saveData(FILEPATH);
            return new ResponseEntity<>("Fire Station added successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to add person", e);
            return new ResponseEntity<>("Failed to add person: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping
    public ResponseEntity<String> updateFirestation(@RequestBody FireStation updateFirestation) throws Exception {
        if ((updateFirestation.getAddress().isEmpty() || updateFirestation.getStation() == 0)) {
            logger.error("Bad request from update fire station - Missing required fields: {} {} ", updateFirestation.getAddress(), updateFirestation.getStation());
            return new ResponseEntity<>("Bad rerquest", HttpStatus.BAD_REQUEST);
        }
        try {
            List<FireStation> fireStations = dataService.getFireStations();
            for (FireStation fireStation : fireStations) {
                if (fireStation.getAddress().equals(updateFirestation.getAddress())) {
                    fireStation.setStation(updateFirestation.getStation());
                    dataService.saveData(FILEPATH);
                    logger.info("Fire station updated successfully");
                    return new ResponseEntity<>("Fire station updated successfully", HttpStatus.OK);
                }
            }
            logger.error("fire station address not found - Existing fire station: {}, {}", updateFirestation.getAddress(), updateFirestation.getStation());
            return new ResponseEntity<>("Fire station address not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Failed to update fire station", e);
            return new ResponseEntity<>("Failed to update fire station: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deleteFirestation(@RequestParam String address)  {

        try {
            List<FireStation> fireStations = dataService.getFireStations();
            boolean removed = fireStations.removeIf(fireStation ->
                    fireStation.getAddress().equals(address));
            if (removed) {
                dataService.saveData(FILEPATH);
                logger.info("Fire station deleted succesfully");
                return new ResponseEntity<>("Fire station deleted succesfully", HttpStatus.OK);
            } else {
                logger.error("Address not found {}", address);
                return new ResponseEntity<>("Address not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Failed to delete fire station");
            return new ResponseEntity<>("Failed to delete fire station : " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping
    public ResponseEntity<FirestationResponse> getPersonsCoveredByFirestation(@RequestParam("stationNumber") int stationNumber) {
        List<Person> personsCovered = dataService.getPersonsByStationNumber(stationNumber);
        List<MedicalRecord> medicalRecords = dataService.getMedicalrecordByPerson(personsCovered);

        long adultCount = medicalRecords.stream()
                .filter(medicalRecord -> CalculateAgeService.calculateAge(medicalRecord.getBirthdate()) >= 18)
                .count();

        long childCount = medicalRecords.stream()
                .filter(medicalRecord -> CalculateAgeService.calculateAge(medicalRecord.getBirthdate()) < 18)
                .count();

        List<PersonInfo> personInfoList = personsCovered.stream()
                .map(person -> new PersonInfo(person.getFirstName(), person.getLastName(), person.getAddress(), person.getPhone()))
                .collect(Collectors.toList());

        FirestationResponse response = new FirestationResponse(personInfoList, adultCount, childCount);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}