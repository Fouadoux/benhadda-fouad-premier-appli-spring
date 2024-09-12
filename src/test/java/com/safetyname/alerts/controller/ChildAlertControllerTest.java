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

@WebMvcTest(ChildAlertContoller.class)
class ChildAlertControllerTest {

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
                new MedicalRecord("John", "Doe", "01/01/2010", Arrays.asList("med1"), Arrays.asList("allergy1")),  // Enfant
                new MedicalRecord("Jane", "Doe", "01/01/1985", Arrays.asList("med2"), Arrays.asList("allergy2"))   // Adulte
        );
    }

    @Test
    void testGetChild_NoPersonFound() throws Exception {
        String address = "123 Main St";
        // Simuler aucune personne trouvée à l'adresse
        when(dataService.getPersonByAddress(address)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/childAlert").param("address", address))
                .andExpect(status().isNotFound());  // Vérifie que le statut est 404 Not Found
    }

    @Test
    void testGetChild_ChildAndFamilyFound() throws Exception {
        String address = "123 Main St";

        // Simuler des personnes et des dossiers médicaux trouvés
        when(dataService.getPersonByAddress(address)).thenReturn(persons);
        when(dataService.getMedicalrecordByPerson(persons)).thenReturn(medicalRecords);

        mockMvc.perform(get("/childAlert").param("address", address))
                .andExpect(status().isOk())  // Vérifie que le statut est 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))  // Vérifie qu'il y a un seul enfant
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].age").value(14))  // Âge calculé
                .andExpect(jsonPath("$[0].family", hasSize(1)))  // Vérifie qu'il y a 1 membre de la famille
                .andExpect(jsonPath("$[0].family[0]").value("Jane Doe"));
    }

    @Test
    void testGetChild_NoChildFound() throws Exception {
        String address = "123 Main St";

        // Simuler uniquement des adultes à l'adresse
        List<MedicalRecord> medicalRecordsNoChild = Arrays.asList(
                new MedicalRecord("Jane", "Doe", "01/01/1985", Arrays.asList("med2"), Arrays.asList("allergy2"))   // Adulte
        );

        when(dataService.getPersonByAddress(address)).thenReturn(persons);
        when(dataService.getMedicalrecordByPerson(persons)).thenReturn(medicalRecordsNoChild);

        mockMvc.perform(get("/childAlert").param("address", address))
                .andExpect(status().isNotFound());  // Vérifie que le statut est 404 Not Found
    }
}
