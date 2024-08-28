package com.safetyname.alerts.controller;



import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.DataService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    private final DataService dataService;

    public PersonController(DataService dataService) {
        this.dataService = dataService;
    }

    // Ajouter une nouvelle personne
    @PostMapping
    public void addPerson(@RequestBody Person person) {
        List<Person> persons = dataService.getPersons();
        persons.add(person);
        dataService.saveData();
    }

    // Mettre à jour une personne existante
    @PutMapping
    public void updatePerson(@RequestBody Person updatedPerson) {
        List<Person> persons = dataService.getPersons();
        for (Person person : persons) {
            if (person.getFirstName().equals(updatedPerson.getFirstName()) &&
                    person.getLastName().equals(updatedPerson.getLastName())) {
                // Mettre à jour les champs
                person.setAddress(updatedPerson.getAddress());
                person.setCity(updatedPerson.getCity());
                person.setZip(updatedPerson.getZip());
                person.setPhone(updatedPerson.getPhone());
                person.setEmail(updatedPerson.getEmail());
            }
        }
        dataService.saveData();
    }

    // Supprimer une personne
    @DeleteMapping
    public void deletePerson(@RequestParam String firstName, @RequestParam String lastName) {
        List<Person> persons = dataService.getPersons();
        boolean removed = persons.removeIf(person ->
                person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)
        );
        if(removed) {
            dataService.saveData();
        }
        else {
            System.out.println("erreur");
        }
    }
}