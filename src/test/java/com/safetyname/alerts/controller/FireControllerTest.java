package com.safetyname.alerts.controller;


import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.DataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FireController.class)
class FireControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataService dataService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Person> persons;
    private List<MedicalRecord> medicalRecords;

    @BeforeEach
    void setUp() {
        // Initialisation des personnes
        persons = Arrays.asList(
                new Person("John", "Doe", "123 Main St", "City1", "john@example.com", 71100, "123-456-7890"),
                new Person("Jane", "Doe", "123 Main St", "City1", "jane@example.com", 71100, "987-654-3210")
        );

        // Initialisation des dossiers médicaux
        medicalRecords = Arrays.asList(
                new MedicalRecord("John", "Doe", "01/01/1985", Arrays.asList("Aspirin"), Arrays.asList("Peanuts")),
                new MedicalRecord("Jane", "Doe", "01/01/1990", Arrays.asList("Paracetamol"), Arrays.asList("Cats"))
        );
    }

    @Test
    void testGetFireInfo_NoPersonFound() throws Exception {
        String address = "123 Main St";
        // Simuler aucune personne trouvée à l'adresse
        when(dataService.getPersonByAddress(address)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/fire").param("address", address))
                .andExpect(status().isOk())  // Vérifie que le statut est OK même si pas de personne
                .andExpect(jsonPath("$.fireInfos", hasSize(0)))  // Vérifie que la liste est vide
                .andExpect(jsonPath("$.station").value(0));  // Pas de station car aucune personne
    }

    @Test
    void testGetFireInfo_PersonAndMedicalRecordFound() throws Exception {
        String address = "123 Main St";
        int stationNumber = 3;  // Simuler le numéro de station de secours

        // Simuler des personnes et des dossiers médicaux trouvés
        when(dataService.getPersonByAddress(address)).thenReturn(persons);
        when(dataService.getMedicalrecordByPerson(persons)).thenReturn(medicalRecords);
        when(dataService.getSationByAddress(address)).thenReturn(stationNumber);

        mockMvc.perform(get("/fire").param("address", address))
                .andExpect(status().isOk())  // Vérifie que le statut est 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fireInfos", hasSize(2)))  // Vérifie qu'il y a 2 personnes dans la réponse
                .andExpect(jsonPath("$.fireInfos[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.fireInfos[0].medications[0]").value("Aspirin"))
                .andExpect(jsonPath("$.fireInfos[0].allergies[0]").value("Peanuts"))
                .andExpect(jsonPath("$.fireInfos[1].lastName").value("Doe"))
                .andExpect(jsonPath("$.fireInfos[1].medications[0]").value("Paracetamol"))
                .andExpect(jsonPath("$.fireInfos[1].allergies[0]").value("Cats"))
                .andExpect(jsonPath("$.station").value(stationNumber));  // Vérifie que la station est correcte
    }

    @Test
    void testGetFireInfo_EmptyMedicalRecords() throws Exception {
        String address = "123 Main St";
        int stationNumber = 3;

        // Simuler des personnes trouvées mais aucun dossier médical
        when(dataService.getPersonByAddress(address)).thenReturn(persons);
        when(dataService.getMedicalrecordByPerson(persons)).thenReturn(Collections.emptyList());
        when(dataService.getSationByAddress(address)).thenReturn(stationNumber);

        mockMvc.perform(get("/fire").param("address", address))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fireInfos", hasSize(0)))  // Vérifie que la liste est vide
                .andExpect(jsonPath("$.station").value(3));  // Vérifie que la station est correcte
    }
}
