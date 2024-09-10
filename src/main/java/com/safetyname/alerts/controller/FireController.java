package com.safetyname.alerts.controller;


import com.safetyname.alerts.dto.FireInfo;
import com.safetyname.alerts.dto.FireResponse;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.safetyname.alerts.utility.Constante.FILEPATH;

@RestController
@RequestMapping("/fire")
public class FireController {


    private static final Logger logger = LogManager.getLogger(FireController.class);
    private DataService dataService;

    public FireController(DataService dataService) {
        this.dataService = dataService;
        this.dataService.readJsonFile(FILEPATH);
    }

    @GetMapping
    public ResponseEntity<FireResponse> getFireInfo(@RequestParam("address") String address)
    {
        logger.info("Requête reçue pour l'adresse' : {}", address);

        List<Person> persons=dataService.getPersonByAddress(address); // je recupere les personne a une address
        List<MedicalRecord> medicalRecords=dataService.getMedicalrecordByPerson(persons); // je recup les dossier medical des personne a l'adresse en arg
        int station = dataService.getSationByAddress(address);

        List<FireInfo> fireInfos = new ArrayList<>();

        for (Person person : persons) {
            for (MedicalRecord record : medicalRecords) {
                if (record.getFirstName().equals(person.getFirstName()) && record.getLastName().equals(person.getLastName())) {
                    FireInfo fireInfo = new FireInfo(
                            person.getLastName(),
                            person.getAddress(),
                            CalculateAgeService.calculateAge(record.getBirthdate()),
                            record.getMedications(),
                            record.getAllergies()
                    );
                    fireInfos.add(fireInfo);
                }
            }
        }

        FireResponse response =new FireResponse(fireInfos,station);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
