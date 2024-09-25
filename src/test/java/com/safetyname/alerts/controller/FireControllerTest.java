package com.safetyname.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.DataService;
import com.safetyname.alerts.service.IDataService;
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

// Import Logger
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Test class for {@link FireController}.
 * <p>
 * This class contains unit tests for the FireController endpoints, verifying
 * different scenarios such as when no persons are found, when persons and medical records are found,
 * and when medical records are empty.
 */
@WebMvcTest(FireController.class)
class FireControllerTest {

    private static final Logger logger = LogManager.getLogger(FireControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IDataService dataService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Person> persons;
    private List<MedicalRecord> medicalRecords;

    /**
     * Sets up the test data before each test method.
     * <p>
     * Initializes the lists of persons and medical records to be used in the tests.
     */
    @BeforeEach
    void setUp() {
        // Initialize persons
        persons = Arrays.asList(
                new Person("John", "Doe", "123 Main St", "City1", "john@example.com", 71100, "123-456-7890"),
                new Person("Jane", "Doe", "123 Main St", "City1", "jane@example.com", 71100, "987-654-3210")
        );

        // Initialize medical records
        medicalRecords = Arrays.asList(
                new MedicalRecord("John", "Doe", "01/01/1985", Arrays.asList("Aspirin"), Arrays.asList("Peanuts")),
                new MedicalRecord("Jane", "Doe", "01/01/1990", Arrays.asList("Paracetamol"), Arrays.asList("Cats"))
        );
    }

    /**
     * Tests the scenario where no persons are found at the given address.
     * <p>
     * Expects an OK status with empty results.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testNoPersonFoundAtAddress() throws Exception {
        String address = "123 Main St";
        logger.info("Testing scenario: No persons found at address {}", address);
        // Simulate no persons found at the address
        when(dataService.getPersonsByAddress(address)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/fire").param("address", address))
                .andExpect(status().isOk())  // Verifies that the status is OK even if no persons are found
                .andExpect(jsonPath("$.fireInfos", hasSize(0)))  // Verifies that the list is empty
                .andExpect(jsonPath("$.station").value(0));  // No station since no persons
    }

    /**
     * Tests the scenario where persons and medical records are found at the given address.
     * <p>
     * Expects an OK status with correct data in the response.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testPersonsAndMedicalRecordsFoundAtAddress() throws Exception {
        String address = "123 Main St";
        int stationNumber = 3;  // Simulate the fire station number
        logger.info("Testing scenario: Persons and medical records found at address {}", address);

        // Simulate persons and medical records found
        when(dataService.getPersonsByAddress(address)).thenReturn(persons);
        when(dataService.getMedicalRecordsByPersons(persons)).thenReturn(medicalRecords);
        when(dataService.getStationByAddress(address)).thenReturn(stationNumber);

        mockMvc.perform(get("/fire").param("address", address))
                .andExpect(status().isOk())  // Verifies that the status is 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fireInfos", hasSize(2)))  // Verifies that there are 2 persons in the response
                .andExpect(jsonPath("$.fireInfos[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.fireInfos[0].medications[0]").value("Aspirin"))
                .andExpect(jsonPath("$.fireInfos[0].allergies[0]").value("Peanuts"))
                .andExpect(jsonPath("$.fireInfos[1].lastName").value("Doe"))
                .andExpect(jsonPath("$.fireInfos[1].medications[0]").value("Paracetamol"))
                .andExpect(jsonPath("$.fireInfos[1].allergies[0]").value("Cats"))
                .andExpect(jsonPath("$.station").value(stationNumber));  // Verifies that the station is correct
    }

    /**
     * Tests the scenario where persons are found but no medical records are available.
     * <p>
     * Expects an OK status with empty medical records.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testPersonsFoundButNoMedicalRecords() throws Exception {
        String address = "123 Main St";
        int stationNumber = 3;
        logger.info("Testing scenario: Persons found but no medical records at address {}", address);

        // Simulate persons found but no medical records
        when(dataService.getPersonsByAddress(address)).thenReturn(persons);
        when(dataService.getMedicalRecordsByPersons(persons)).thenReturn(Collections.emptyList());
        when(dataService.getStationByAddress(address)).thenReturn(stationNumber);

        mockMvc.perform(get("/fire").param("address", address))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fireInfos", hasSize(0)))  // Verifies that the list is empty
                .andExpect(jsonPath("$.station").value(stationNumber));  // Verifies that the station is correct
    }
}
