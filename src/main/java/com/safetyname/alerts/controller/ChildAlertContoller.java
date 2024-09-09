package com.safetyname.alerts.controller;


import com.safetyname.alerts.dto.ChildResponse;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.CalculateAgeService;
import com.safetyname.alerts.service.DataService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.safetyname.alerts.utility.Constante.FILEPATH;

@RestController
@RequestMapping("/childAlert")
public class ChildAlertContoller {

    private static final Logger logger = LogManager.getLogger(ChildAlertContoller.class);

    private DataService dataService;

    public ChildAlertContoller(DataService dataService) {
        this.dataService = dataService;
        this.dataService.readJsonFile(FILEPATH);
    }

    @GetMapping
    public ResponseEntity<List<ChildResponse>> getChhil(@RequestParam("address") String address) {
        logger.info("Recherche d'enfants à l'adresse : {}", address);

        List<Person> person = dataService.getPersonByAddress(address);
        if (person.isEmpty()) {
            logger.warn("Aucune personne trouvée à l'adresse : {}", address);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Aucune personne trouvée
        }

        List<MedicalRecord> medicalRecords = dataService.getMedicalrecordByPerson(person);

        List<String> famille = medicalRecords.stream()
                .filter(medicalRecord -> CalculateAgeService.calculateAge(medicalRecord.getBirthdate()) >= 18)
                .map(medicalRecord -> medicalRecord.getFirstName() + " " + medicalRecord.getLastName())
                .collect(Collectors.toList());

        List<ChildResponse> childs = medicalRecords.stream()
                .map(medicalRecord -> {
                    int age = CalculateAgeService.calculateAge(medicalRecord.getBirthdate());
                    return new AbstractMap.SimpleEntry<>(medicalRecord, age);
                })
                .filter(entry -> entry.getValue() < 18)  // Filtrer les mineurs
                .map(entry -> new ChildResponse(entry.getKey().getFirstName(),
                        entry.getKey().getLastName(),
                        entry.getValue(),
                        famille))
                .collect(Collectors.toList());

        if (childs.isEmpty()) {
            logger.warn("Aucun enfant trouvé à l'adresse : {}", address);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        logger.info("Nombre d'enfants trouvés : {}", childs.size());
        return new ResponseEntity<>(childs, HttpStatus.OK);
    }

}
