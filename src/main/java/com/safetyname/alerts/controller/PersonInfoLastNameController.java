package com.safetyname.alerts.controller;



import com.safetyname.alerts.dto.PersonInfoLastNameResponse;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.CalculateAgeService;
import com.safetyname.alerts.service.DataService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.stream.Collectors;

import static com.safetyname.alerts.utility.Constante.FILEPATH;

@RestController
@RequestMapping("/personInfolastName")
public class PersonInfoLastNameController {

    private static final Logger logger = LogManager.getLogger(PersonInfoLastNameController.class);

    private DataService dataService;

    public PersonInfoLastNameController(DataService dataService) {
        this.dataService = dataService;
        this.dataService.readJsonFile(FILEPATH);
    }

    @GetMapping("/{lastName}")
    public ResponseEntity<List<PersonInfoLastNameResponse>> getPersonInfolastName (@PathVariable String lastName) {
        if(lastName == null || lastName.trim().isEmpty()){
            logger.warn("Last name is empty : bad request");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Person> personByLastName = dataService.getPersonByLastName(lastName);
        if (personByLastName.isEmpty()) {
            logger.warn("Aucune personne trouvée pour le nom de famille : {}", lastName);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 404 Not Found si aucune personne trouvée
        }

        List<MedicalRecord> medicalRecords = dataService.getMedicalrecordByPerson(personByLastName);
        if (medicalRecords.isEmpty()) {
            logger.warn("Aucun dossier médical trouvé pour les personnes avec le nom de famille : {}", lastName);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 404 Not Found si aucun dossier médical trouvé
        }

        List<PersonInfoLastNameResponse> responses = personByLastName.stream()
                .flatMap(person -> medicalRecords.stream()
                        .filter(record -> record.getLastName().equals(person.getLastName()) && record.getFirstName().equals(person.getFirstName()) )  // Filtrer directement
                        .map(record -> new PersonInfoLastNameResponse(
                                person.getLastName(),
                                person.getAddress(),
                                CalculateAgeService.calculateAge(record.getBirthdate()),
                                person.getEmail(),
                                record.getMedications(),
                                record.getAllergies()
                        )))
                .collect(Collectors.toList());

        if (responses.isEmpty()) {
            logger.warn("Aucune correspondance trouvée entre les personnes et les dossiers médicaux pour le nom de famille : {}", lastName);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(responses, HttpStatus.OK);

    }
}
