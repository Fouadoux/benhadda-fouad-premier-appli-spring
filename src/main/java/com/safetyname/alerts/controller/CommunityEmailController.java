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
@RequestMapping("/communityEmail")
public class CommunityEmailController {

    private static final Logger logger = LogManager.getLogger(CommunityEmailController.class);
    private  DataService dataService;

    public CommunityEmailController(DataService dataService) {
        this.dataService = dataService;
        this.dataService.readJsonFile(FILEPATH);
    }

    @GetMapping
    public ResponseEntity<List<String>> getEmailByCity(@RequestParam("city") String city){
        logger.info("Requête reçue pour la ville : {}", city);
        if(city == null || city.trim().isEmpty()){
            logger.warn("bad request");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List <Person> persons=dataService.getPersons();
        if (persons.isEmpty()) {
            logger.warn("Aucune personne trouvée pour la ville : {}", city);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 404 Not Found si aucune personne trouvée
        }


        List<String> personEmails = persons.stream()
                .filter(person -> person.getCity().equalsIgnoreCase(city))
                .map(Person::getEmail)
                .collect(Collectors.toList());

        if(personEmails.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(personEmails, HttpStatus.OK);
    }
}
