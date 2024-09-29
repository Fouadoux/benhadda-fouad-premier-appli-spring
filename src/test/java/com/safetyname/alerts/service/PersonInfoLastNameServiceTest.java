package com.safetyname.alerts.service;

import com.safetyname.alerts.dto.PersonInfoLastNameResponse;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.IDataService;
import com.safetyname.alerts.service.PersonInfoLastNameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PersonInfoLastNameServiceTest {

    @Mock
    private IDataService dataService;

    @InjectMocks
    private PersonInfoLastNameService personInfoLastNameService;

    private List<Person> persons;
    private List<MedicalRecord> medicalRecords;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialisation des données de test
        persons = Arrays.asList(
                new Person("John", "Doe", "123 Main St", "City", "john@example.com", 71100, "123-456-7890"),
                new Person("Jane", "Doe", "123 Main St", "City", "jane@example.com", 71100, "987-654-3210")
        );
        List<String> medications = Arrays.asList("Aspirin");  // Correct
        List<String> allergies = Arrays.asList("Peanuts");
        List<String> medications2 = Arrays.asList("Paracetamol");  // Correct
        List<String> allergies2 = Arrays.asList("Cats");
        medicalRecords = Arrays.asList(
                new MedicalRecord("John", "Doe", "01/01/1985",medications, allergies),
                new MedicalRecord("Jane", "Doe", "01/01/1990", medications2, allergies2)
        );
    }

    /**
     * Teste la récupération réussie des informations sur les personnes par nom de famille.
     */
    @Test
    void testGetPersonInfoLastNameSuccess() {
        // Simulation du comportement du dataService
        when(dataService.getPersonsByLastName("Doe")).thenReturn(persons);
        when(dataService.getMedicalRecordsByPersons(persons)).thenReturn(medicalRecords);

        // Appel du service
        List<PersonInfoLastNameResponse> responses = personInfoLastNameService.getPersonInfoLastNameService("Doe");

        // Vérification des résultats
        assertFalse(responses.isEmpty());
        assertEquals(2, responses.size());
        assertEquals("Doe", responses.get(0).getLastName());
        assertEquals("123 Main St", responses.get(0).getAddress());
        assertEquals(39, responses.get(0).getAge());  // Supposons que l'âge soit calculé correctement
        assertEquals(Arrays.asList("Aspirin"), responses.get(0).getMedications());
        assertEquals(Arrays.asList("Peanuts"), responses.get(0).getAllergies());
    }

    /**
     * Teste le cas où aucune personne n'est trouvée avec le nom de famille donné.
     */
    @Test
    void testGetPersonInfoLastNameNotFound() {
        // Simulation du comportement du dataService
        when(dataService.getPersonsByLastName("Doe")).thenReturn(Collections.emptyList());

        // Appel du service
        List<PersonInfoLastNameResponse> responses = personInfoLastNameService.getPersonInfoLastNameService("Doe");

        // Vérification que la liste est vide
        assertTrue(responses.isEmpty());
    }

    /**
     * Teste le cas où aucun dossier médical n'est trouvé pour les personnes trouvées.
     */
    @Test
    void testGetPersonInfoNoMedicalRecordsFound() {
        // Simulation du comportement du dataService
        when(dataService.getPersonsByLastName("Doe")).thenReturn(persons);
        when(dataService.getMedicalRecordsByPersons(persons)).thenReturn(Collections.emptyList());

        // Appel du service
        List<PersonInfoLastNameResponse> responses = personInfoLastNameService.getPersonInfoLastNameService("Doe");

        // Vérification que la liste est vide
        assertTrue(responses.isEmpty());
    }
}
