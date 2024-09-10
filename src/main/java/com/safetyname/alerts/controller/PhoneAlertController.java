package com.safetyname.alerts.controller;


import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.DataService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.safetyname.alerts.utility.Constante.FILEPATH;

@RestController
@RequestMapping("phoneAlert")
public class PhoneAlertController {

    private static final Logger logger = LogManager.getLogger(PhoneAlertController.class);

    private DataService dataService;

    public PhoneAlertController(DataService dataService) {
        this.dataService = dataService;
        this.dataService.readJsonFile(FILEPATH);
    }

    @GetMapping
    public ResponseEntity<List<String>> getPhoneNumberByFireStation(@RequestParam("firestation") int stationNumber){
        logger.info("Requête reçue pour la caserne numéro : {}", stationNumber);

        List<Person> persons =dataService.getPersonsByStationNumber(stationNumber);
        if (persons == null || persons.isEmpty()) {
            logger.warn("Aucune personne trouvée pour la caserne numéro : {}", stationNumber);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List <String> personPhoneNumber= persons.stream()
                .map(Person::getPhone)
                .collect(Collectors.toList());

        return new ResponseEntity<>(personPhoneNumber, HttpStatus.OK);

    }

}
