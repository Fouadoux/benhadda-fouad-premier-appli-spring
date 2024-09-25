package com.safetyname.alerts.controller;

import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.DataService;
import com.safetyname.alerts.service.IDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

/**
 * Test class for {@link PhoneAlertController}.
 * <p>
 * This class contains unit tests for the PhoneAlertController endpoints,
 * verifying scenarios where no persons are found and when persons are found
 * for a given fire station number.
 */
@WebMvcTest(PhoneAlertController.class)
class PhoneAlertControllerTest {

    private static final Logger logger = LogManager.getLogger(PhoneAlertControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IDataService dataService;

    private List<Person> persons;

    /**
     * Sets up the test data before each test method.
     * <p>
     * Initializes the list of persons to be used in the tests.
     */
    @BeforeEach
    void setUp() {
        // Initialize persons
        persons = Arrays.asList(
                new Person("John", "Doe", "123 Main St", "City1", "john@example.com", 71100, "123-456-7890"),
                new Person("Jane", "Doe", "456 Oak St", "City1", "jane@example.com", 71100, "987-654-3210")
        );
    }

    /**
     * Tests the scenario where no persons are found for the given fire station number.
     * <p>
     * Expects a 404 Not Found status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetPhoneNumberByFireStationNoPersonFound() throws Exception {
        logger.info("Testing retrieval of phone numbers when no persons are found for the fire station.");

        int stationNumber = 1;

        // Simulate no persons found for this fire station
        when(dataService.getPersonsByStationNumber(stationNumber)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/phoneAlert").param("firestation", String.valueOf(stationNumber)))
                .andExpect(status().isNotFound());  // Verifies that the status is 404 Not Found
    }

    /**
     * Tests the scenario where persons are found for the given fire station number.
     * <p>
     * Expects a 200 OK status and verifies the returned phone numbers.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetPhoneNumberByFireStationPersonsFound() throws Exception {
        logger.info("Testing retrieval of phone numbers when persons are found for the fire station.");

        int stationNumber = 1;

        // Simulate persons found for this fire station
        when(dataService.getPersonsByStationNumber(stationNumber)).thenReturn(persons);

        mockMvc.perform(get("/phoneAlert").param("firestation", String.valueOf(stationNumber)))
                .andExpect(status().isOk())  // Verifies that the status is 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))  // Verifies that there are 2 phone numbers
                .andExpect(jsonPath("$[0]").value("123-456-7890"))  // Verifies the first phone number
                .andExpect(jsonPath("$[1]").value("987-654-3210"));  // Verifies the second phone number
    }
}
