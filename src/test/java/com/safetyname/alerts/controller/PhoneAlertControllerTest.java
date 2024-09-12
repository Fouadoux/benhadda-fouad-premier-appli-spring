package com.safetyname.alerts.controller;

import com.safetyname.alerts.controller.PhoneAlertController;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.DataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

@WebMvcTest(PhoneAlertController.class)
class PhoneAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataService dataService;

    private List<Person> persons;

    @BeforeEach
    void setUp() {
        // Initialisation des personnes
        persons = Arrays.asList(
                new Person("John", "Doe", "123 Main St", "City1", "john@example.com", 71100, "123-456-7890"),
                new Person("Jane", "Doe", "456 Oak St", "City1", "jane@example.com", 71100, "987-654-3210")
        );
    }

    @Test
    void testGetPhoneNumberByFireStation_NoPersonFound() throws Exception {
        int stationNumber = 1;

        // Simuler aucune personne trouvée pour cette caserne
        when(dataService.getPersonsByStationNumber(stationNumber)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/phoneAlert").param("firestation", String.valueOf(stationNumber)))
                .andExpect(status().isNotFound());  // Vérifie que le statut est 404 Not Found
    }

    @Test
    void testGetPhoneNumberByFireStation_PersonsFound() throws Exception {
        int stationNumber = 1;

        // Simuler des personnes trouvées pour cette caserne
        when(dataService.getPersonsByStationNumber(stationNumber)).thenReturn(persons);

        mockMvc.perform(get("/phoneAlert").param("firestation", String.valueOf(stationNumber)))
                .andExpect(status().isOk())  // Vérifie que le statut est 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))  // Vérifie qu'il y a 2 numéros de téléphone
                .andExpect(jsonPath("$[0]").value("123-456-7890"))  // Vérifie le premier numéro de téléphone
                .andExpect(jsonPath("$[1]").value("987-654-3210"));  // Vérifie le deuxième numéro de téléphone
    }
}
