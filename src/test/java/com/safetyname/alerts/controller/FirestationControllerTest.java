package com.safetyname.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetyname.alerts.entity.FireStation;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.DataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FirestationController.class)
class FirestationControllerTest {


    @Autowired
    MockMvc mockMvc;

    @MockBean
    DataService dataService;

    ObjectMapper objectMapper = new ObjectMapper();
    List<FireStation> fireStations;

    @BeforeEach
    void setUp() {
        fireStations=new ArrayList<>();
        fireStations.add(new FireStation("19 rue pasteur",4));
        when(dataService.getFireStations()).thenReturn(fireStations);
    }

    @Test
    void testUpdateFirestation_Success() throws Exception {
        FireStation fireStation = new FireStation("19 rue pasteur",5);
        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isOk())
                .andExpect(content().string("Fire station updated successfully"));
        verify(dataService).saveData(anyString());
    }

    @Test
    void testUpdateFirestationTest_Fail() throws Exception{
        FireStation fireStation = new FireStation("105 avenue des champs",4);
        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Fire station address not found"));
    }

    @Test
    public void testUpdateFirestation_InvalidData() throws Exception {
        FireStation fireStation = new FireStation("",5);

        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateFirestation_NoContent() throws Exception{

        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddFirestation_success() throws Exception {
        FireStation fireStation = new FireStation("34 rue de beaune",2);
        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Fire Station added successfully"));

        verify(dataService).saveData(anyString());
    }

    @Test
    void testAddFirestation_InvalidData()throws Exception{
        FireStation fireStation = new FireStation("",4);
        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isBadRequest());

    }
    @Test
    void testFirestation_Conflit()throws Exception{
        FireStation fireStation = new FireStation("19 rue pasteur",4);
        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isConflict());

    }

    @Test
    public void testDeleteFirestation_Success() throws Exception {

        mockMvc.perform(delete("/firestation")
                        .param("address","19 rue pasteur" ))
                .andExpect(status().isOk())
                .andExpect(content().string("Fire station deleted succesfully"));

        verify(dataService).saveData(anyString());
    }
    @Test
    public void testDeleteFirestation_NotFound() throws Exception {

        mockMvc.perform(delete("/firestation")
                        .param("address", "19 rue pastis"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Address not found"));
    }

    @Test
    void testGetPersonsCoveredByFirestation_OK() throws Exception {

        // Simuler les données de retour du DataService pour les personnes
        List<Person> personsCovered = Arrays.asList(
                new Person("John", "Doe", "123 Main St", "City1", "john@example.com", 71100, "123-456-7890"),
                new Person("Jane", "Smith", "456 Oak St", "City2", "jane@example.com", 71100, "987-654-3210")
        );
        when(dataService.getPersonsByStationNumber(1)).thenReturn(personsCovered);

        // Simuler les dossiers médicaux associés aux personnes
        List<MedicalRecord> medicalRecords = Arrays.asList(
                new MedicalRecord("John", "Doe", "01/01/1980", Arrays.asList("med1"), Arrays.asList("allergy1")),
                new MedicalRecord("Jane", "Smith", "01/01/2015", Arrays.asList("med2"), Arrays.asList("allergy2"))
        );
        when(dataService.getMedicalrecordByPerson(personsCovered)).thenReturn(medicalRecords);

        // Effectuer la requête GET
        mockMvc.perform(get("/firestation?stationNumber=1")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.persons[0].firstName").value("John"))  // Valider le premier prénom
                .andExpect(jsonPath("$.persons[0].lastName").value("Doe"))  // Valider le premier nom de famille
                .andExpect(jsonPath("$.persons[0].address").value("123 Main St"))  // Valider l'adresse
                .andExpect(jsonPath("$.persons[0].phone").value("123-456-7890"))  // Valider le numéro de téléphone
                .andExpect(jsonPath("$.persons[1].firstName").value("Jane"))  // Valider le deuxième prénom
                .andExpect(jsonPath("$.persons[1].lastName").value("Smith"))  // Valider le deuxième nom de famille
                .andExpect(jsonPath("$.adultCount").value(1))  // Valider le nombre d'adultes
                .andExpect(jsonPath("$.childCount").value(1));
    }
    @Test
    void testGetPersonsCoveredByFirestation_NotFound_NoPersons() throws Exception {
        // Simuler le cas où aucune personne n'est trouvée pour la caserne
        when(dataService.getPersonsByStationNumber(1)).thenReturn(Collections.emptyList());

        // Effectuer la requête GET et attendre un statut NOT_FOUND
        mockMvc.perform(get("/firestation?stationNumber=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPersonsCoveredByFirestation_NotFound_NoMedicalRecords() throws Exception {
        // Simuler des personnes mais aucun dossier médical correspondant
        List<Person> personsCovered = Arrays.asList(
                new Person("John", "Doe", "123 Main St", "City1", "john@example.com",5654, "123-456-7890")
        );
        when(dataService.getPersonsByStationNumber(1)).thenReturn(personsCovered);

        // Simuler le cas où aucun dossier médical n'est trouvé pour les personnes
        when(dataService.getMedicalrecordByPerson(personsCovered)).thenReturn(Collections.emptyList());

        // Effectuer la requête GET et attendre un statut NOT_FOUND
        mockMvc.perform(get("/firestation?stationNumber=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}