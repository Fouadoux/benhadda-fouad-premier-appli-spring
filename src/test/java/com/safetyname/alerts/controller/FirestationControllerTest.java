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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Test class for {@link FirestationController}.
 * <p>
 * This class contains unit tests for the FirestationController endpoints,
 * verifying different scenarios such as successful updates, additions,
 * deletions, and retrievals of fire station data.
 */
@WebMvcTest(FirestationController.class)
class FirestationControllerTest {

    private static final Logger logger = LogManager.getLogger(FirestationControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataService dataService;

    private ObjectMapper objectMapper = new ObjectMapper();
    private List<FireStation> fireStations;

    /**
     * Sets up the test data before each test method.
     * <p>
     * Initializes the list of fire stations to be used in the tests.
     */
    @BeforeEach
    void setUp() {
        fireStations = new ArrayList<>();
        fireStations.add(new FireStation("19 Pasteur Street", 4));
        when(dataService.getFireStations()).thenReturn(fireStations);
    }

    /**
     * Tests the successful update of a fire station.
     * <p>
     * Expects a 200 OK status and verifies that the data service saves the data.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testUpdateFirestationSuccess() throws Exception {
        logger.info("Testing successful fire station update.");
        FireStation fireStation = new FireStation("19 Pasteur Street", 5);
        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isOk())
                .andExpect(content().string("Fire station updated successfully"));
        verify(dataService).saveData();
    }

    /**
     * Tests updating a fire station that does not exist.
     * <p>
     * Expects a 404 Not Found status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testUpdateFirestationFail() throws Exception {
        logger.info("Testing fire station update with non-existing address.");
        FireStation fireStation = new FireStation("105 Champs Avenue", 4);
        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Fire station address not found"));
    }

    /**
     * Tests updating a fire station with invalid data.
     * <p>
     * Expects a 400 Bad Request status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    public void testUpdateFirestationInvalidData() throws Exception {
        logger.info("Testing fire station update with invalid data.");
        FireStation fireStation = new FireStation("", 5);

        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests updating a fire station with no content.
     * <p>
     * Expects a 400 Bad Request status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testUpdateFirestationNoContent() throws Exception {
        logger.info("Testing fire station update with no content.");
        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests the successful addition of a new fire station.
     * <p>
     * Expects a 201 Created status and verifies that the data service saves the data.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testAddFirestationSuccess() throws Exception {
        logger.info("Testing successful addition of a new fire station.");
        FireStation fireStation = new FireStation("34 Beaune Street", 2);
        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Fire Station added successfully"));

        verify(dataService).saveData();
    }

    /**
     * Tests adding a fire station with invalid data.
     * <p>
     * Expects a 400 Bad Request status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testAddFirestationInvalidData() throws Exception {
        logger.info("Testing fire station addition with invalid data.");
        FireStation fireStation = new FireStation("", 4);
        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests adding a fire station that already exists.
     * <p>
     * Expects a 409 Conflict status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testAddFirestationConflict() throws Exception {
        logger.info("Testing addition of a fire station that already exists.");
        FireStation fireStation = new FireStation("19 Pasteur Street", 4);
        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isConflict());
    }

    /**
     * Tests the successful deletion of a fire station.
     * <p>
     * Expects a 200 OK status and verifies that the data service saves the data.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    public void testDeleteFirestationSuccess() throws Exception {
        logger.info("Testing successful deletion of a fire station.");
        mockMvc.perform(delete("/firestation")
                        .param("address", "19 Pasteur Street"))
                .andExpect(status().isOk())
                .andExpect(content().string("Fire station deleted successfully"));

        verify(dataService).saveData();
    }

    /**
     * Tests deleting a fire station with an address that does not exist.
     * <p>
     * Expects a 404 Not Found status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    public void testDeleteFirestationNotFound() throws Exception {
        logger.info("Testing deletion of a non-existing fire station.");
        mockMvc.perform(delete("/firestation")
                        .param("address", "19 Pastis Street"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Address not found"));
    }

    /**
     * Tests retrieving persons covered by a fire station.
     * <p>
     * Expects a 200 OK status and verifies the returned data.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetPersonsCoveredByFirestationSuccess() throws Exception {
        logger.info("Testing retrieval of persons covered by a fire station.");
        // Simulate data returned by DataService for persons
        List<Person> personsCovered = Arrays.asList(
                new Person("John", "Doe", "123 Main St", "City1", "john@example.com", 71100, "123-456-7890"),
                new Person("Jane", "Smith", "456 Oak St", "City2", "jane@example.com", 71100, "987-654-3210")
        );
        when(dataService.getPersonsByStationNumber(1)).thenReturn(personsCovered);

        // Simulate medical records associated with the persons
        List<MedicalRecord> medicalRecords = Arrays.asList(
                new MedicalRecord("John", "Doe", "01/01/1980", Arrays.asList("med1"), Arrays.asList("allergy1")),
                new MedicalRecord("Jane", "Smith", "01/01/2015", Arrays.asList("med2"), Arrays.asList("allergy2"))
        );
        when(dataService.getMedicalRecordsByPersons(personsCovered)).thenReturn(medicalRecords);

        // Perform the GET request
        mockMvc.perform(get("/firestation?stationNumber=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.persons[0].firstName").value("John"))  // Validate first person's first name
                .andExpect(jsonPath("$.persons[0].lastName").value("Doe"))    // Validate first person's last name
                .andExpect(jsonPath("$.persons[0].address").value("123 Main St"))  // Validate address
                .andExpect(jsonPath("$.persons[0].phone").value("123-456-7890"))   // Validate phone number
                .andExpect(jsonPath("$.persons[1].firstName").value("Jane"))  // Validate second person's first name
                .andExpect(jsonPath("$.persons[1].lastName").value("Smith"))  // Validate second person's last name
                .andExpect(jsonPath("$.adultCount").value(1))  // Validate the number of adults
                .andExpect(jsonPath("$.childCount").value(1)); // Validate the number of children
    }

    /**
     * Tests retrieving persons covered by a fire station when no persons are found.
     * <p>
     * Expects a 404 Not Found status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetPersonsCoveredByFirestationNotFoundNoPersons() throws Exception {
        logger.info("Testing retrieval when no persons are found for a fire station.");
        // Simulate no persons found for the station
        when(dataService.getPersonsByStationNumber(1)).thenReturn(Collections.emptyList());

        // Perform the GET request and expect a NOT_FOUND status
        mockMvc.perform(get("/firestation?stationNumber=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests retrieving persons covered by a fire station when no medical records are found.
     * <p>
     * Expects a 404 Not Found status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetPersonsCoveredByFirestationNotFoundNoMedicalRecords() throws Exception {
        logger.info("Testing retrieval when no medical records are found for persons.");
        // Simulate persons found but no corresponding medical records
        List<Person> personsCovered = Arrays.asList(
                new Person("John", "Doe", "123 Main St", "City1", "john@example.com", 5654, "123-456-7890")
        );
        when(dataService.getPersonsByStationNumber(1)).thenReturn(personsCovered);

        // Simulate no medical records found for the persons
        when(dataService.getMedicalRecordsByPersons(personsCovered)).thenReturn(Collections.emptyList());

        // Perform the GET request and expect a NOT_FOUND status
        mockMvc.perform(get("/firestation?stationNumber=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
