package com.safetyname.alerts.controller;

import com.safetyname.alerts.dto.FloodResponse;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.CalculateAgeService;
import com.safetyname.alerts.service.DataService;
import com.safetyname.alerts.service.IDataService;
import com.safetyname.alerts.service.IFloodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Test class for {@link FloodController}.
 * <p>
 * This class contains unit tests for the FloodController endpoints, verifying
 * different scenarios such as successful retrieval of households by station,
 * handling of bad requests, and cases where no data is found.
 */
@WebMvcTest(FloodController.class)
class FloodControllerTest {

    private static final Logger logger = LogManager.getLogger(FloodControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IFloodService floodService;

    private List<FloodResponse> floodResponses1;
    private List<FloodResponse> floodResponses2;
    private Map<String,List<FloodResponse>> householdsByAddress;
    /**
     * Sets up the test data before each test method.
     * <p>
     * Initializes the lists of persons and medical records to be used in the tests.
     */
    @BeforeEach
    void setUp() {
        // Initialize test data
        List<String> medications1=Arrays.asList("medical1","medial2");
        List<String> medications2=Arrays.asList("medical1","medial2");
        List<String> medications3=Arrays.asList("medical1","medial2");
        List<String> allergies1=Arrays.asList("medical1","medial2");
        List<String> allergies2=Arrays.asList("medical1","medial2");
        List<String> allergies3=Arrays.asList("medical1","medial2");
        floodResponses1 = Arrays.asList(
                new FloodResponse("John", "Doe", "123", 34, medications1, allergies1),
                new FloodResponse("Bob", "Doe", "456 Elm St", 12, medications3,allergies3)
        );
        floodResponses2 = Arrays.asList(
                new FloodResponse("Jane", "Smith", "987", 33, medications2, allergies2)
        );
        householdsByAddress = new HashMap<>();
        householdsByAddress.put("123 Main St",floodResponses1);

        householdsByAddress.put("456 Elm St", floodResponses2);

    }

    /**
     * Tests the successful retrieval of households by station numbers.
     * <p>
     * Expects a 200 OK status and verifies that the correct data is returned.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetHouseholdsByStationSuccess() throws Exception {
        logger.info("Testing successful retrieval of households by station numbers.");
        List<Integer> stations=Arrays.asList(1,2);
        when(floodService.getFloodService(stations)).thenReturn(householdsByAddress);

        mockMvc.perform(get("/flood/stations")
                        .param("stations", "1,2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.['123 Main St']", hasSize(2)))  // Verify 2 households at "123 Main St"
                .andExpect(jsonPath("$.['456 Elm St']", hasSize(1)))  // Verify 1 household at "456 Elm St"
                .andExpect(jsonPath("$.['123 Main St'][0].lastName").value("Doe"))    // Check first resident's lastName
                .andExpect(jsonPath("$.['456 Elm St'][0].lastName").value("Smith"));  // Check second address resident's lastName
    }

    /**
     * Tests the handling of a bad request when no station numbers are provided.
     * <p>
     * Expects a 400 Bad Request status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetHouseholdsByStation_BadRequestNoStations() throws Exception {
        logger.info("Testing bad request handling when no station numbers are provided.");

        mockMvc.perform(get("/flood/stations"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests the scenario where no addresses are found for the given station number.
     * <p>
     * Expects a 404 Not Found status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetHouseholdsByStationNotFoundNoAddresses() throws Exception {
        logger.info("Testing not found scenario when no addresses are found for the given station number.");

       // when(dataService.getAddressesByStationNumber(1)).thenReturn(Collections.emptySet());

        mockMvc.perform(get("/flood/stations")
                        .param("stations", "1"))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests the scenario where no persons are found at the addresses associated with the station.
     * <p>
     * Expects a 404 Not Found status.
     *
     * @throws Exception if an error occurs during the request.
     */

    void testGetHouseholdsByStationNotFoundNoPersons() throws Exception {
        logger.info("Testing not found scenario when no persons are found at the addresses associated with the station.");

     //   when(dataService.getAddressesByStationNumber(1)).thenReturn(Collections.singleton("123 Main St"));
     //   when(dataService.getPersonsByAddress("123 Main St")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/flood/stations")
                        .param("stations", "1"))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests the scenario where persons are found but no medical records are available.
     * <p>
     * Expects a 200 OK status with default values for age, medications, and allergies.
     *
     * @throws Exception if an error occurs during the request.
     */

    void testGetHouseholdsByStationSuccessNoMedicalRecords() throws Exception {
        logger.info("Testing successful retrieval when persons are found but no medical records are available.");

     /*   when(dataService.getAddressesByStationNumber(1)).thenReturn(Collections.singleton("123 Main St"));
        when(dataService.getPersonsByAddress("123 Main St")).thenReturn(Arrays.asList(personList.get(0), personList.get(1)));
        when(dataService.getMedicalRecordsByPersons(Arrays.asList(personList.get(0), personList.get(1)))).thenReturn(Collections.emptyList());*/

        mockMvc.perform(get("/flood/stations")
                        .param("stations", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['123 Main St']", hasSize(2)))
                .andExpect(jsonPath("$.['123 Main St'][0].age").value(0))
                .andExpect(jsonPath("$.['123 Main St'][0].medications", hasSize(0)))
                .andExpect(jsonPath("$.['123 Main St'][0].allergies", hasSize(0)));
    }
}
