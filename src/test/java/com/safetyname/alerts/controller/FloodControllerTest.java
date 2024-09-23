package com.safetyname.alerts.controller;

import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.CalculateAgeService;
import com.safetyname.alerts.service.DataService;
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
    private DataService dataService;

    private List<Person> personList;
    private List<MedicalRecord> medicalRecords;

    /**
     * Sets up the test data before each test method.
     * <p>
     * Initializes the lists of persons and medical records to be used in the tests.
     */
    @BeforeEach
    void setUp() {
        // Initialize test data
        personList = Arrays.asList(
                new Person("John", "Doe", "123 Main St", "City", "john@example.com", 71100, "123-456-7890"),
                new Person("Jane", "Doe", "123 Main St", "City", "jane@example.com", 71100, "987-654-3210"),
                new Person("Bob", "Smith", "456 Elm St", "City", "bob@example.com", 71100, "555-555-5555")
        );

        medicalRecords = Arrays.asList(
                new MedicalRecord("John", "Doe", "01/01/1985", Arrays.asList("Aspirin"), Arrays.asList("Peanuts")),
                new MedicalRecord("Jane", "Doe", "01/01/1990", Arrays.asList("Paracetamol"), Arrays.asList("Cats")),
                new MedicalRecord("Bob", "Smith", "01/01/1980", Arrays.asList("Ibuprofen"), Arrays.asList("Pollen"))
        );

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

        // Mock methods of DataService
        when(dataService.getAddressesByStationNumber(1)).thenReturn(Collections.singleton("123 Main St"));
        when(dataService.getAddressesByStationNumber(2)).thenReturn(Collections.singleton("456 Elm St"));
        when(dataService.getPersonsByAddress("123 Main St")).thenReturn(Arrays.asList(personList.get(0), personList.get(1)));
        when(dataService.getPersonsByAddress("456 Elm St")).thenReturn(Collections.singletonList(personList.get(2)));
        when(dataService.getMedicalRecordsByPersons(anyList())).thenReturn(medicalRecords);

        // Mock CalculateAgeService
        try (MockedStatic<CalculateAgeService> mockedStatic = Mockito.mockStatic(CalculateAgeService.class)) {
            mockedStatic.when(() -> CalculateAgeService.calculateAge("01/01/1985")).thenReturn(38);
            mockedStatic.when(() -> CalculateAgeService.calculateAge("01/01/1990")).thenReturn(33);
            mockedStatic.when(() -> CalculateAgeService.calculateAge("01/01/1980")).thenReturn(43);

            mockMvc.perform(get("/flood/stations")
                            .param("stations", "1", "2"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.['123 Main St']", hasSize(2)))
                    .andExpect(jsonPath("$.['123 Main St'][0].firstName", anyOf(is("John"), is("Jane"))))
                    .andExpect(jsonPath("$.['123 Main St'][0].lastName", is("Doe")))
                    .andExpect(jsonPath("$.['123 Main St'][0].phone", anyOf(is("123-456-7890"), is("987-654-3210"))))
                    .andExpect(jsonPath("$.['123 Main St'][0].age", anyOf(is(38), is(33))))
                    .andExpect(jsonPath("$.['123 Main St'][0].medications", anyOf(hasItem("Aspirin"), hasItem("Paracetamol"))))
                    .andExpect(jsonPath("$.['123 Main St'][0].allergies", anyOf(hasItem("Peanuts"), hasItem("Cats"))))
                    .andExpect(jsonPath("$.['456 Elm St']", hasSize(1)))
                    .andExpect(jsonPath("$.['456 Elm St'][0].firstName").value("Bob"))
                    .andExpect(jsonPath("$.['456 Elm St'][0].lastName").value("Smith"))
                    .andExpect(jsonPath("$.['456 Elm St'][0].phone").value("555-555-5555"))
                    .andExpect(jsonPath("$.['456 Elm St'][0].age").value(43))
                    .andExpect(jsonPath("$.['456 Elm St'][0].medications[0]").value("Ibuprofen"))
                    .andExpect(jsonPath("$.['456 Elm St'][0].allergies[0]").value("Pollen"));
        }
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

        when(dataService.getAddressesByStationNumber(1)).thenReturn(Collections.emptySet());

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
    @Test
    void testGetHouseholdsByStationNotFoundNoPersons() throws Exception {
        logger.info("Testing not found scenario when no persons are found at the addresses associated with the station.");

        when(dataService.getAddressesByStationNumber(1)).thenReturn(Collections.singleton("123 Main St"));
        when(dataService.getPersonsByAddress("123 Main St")).thenReturn(Collections.emptyList());

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
    @Test
    void testGetHouseholdsByStationSuccessNoMedicalRecords() throws Exception {
        logger.info("Testing successful retrieval when persons are found but no medical records are available.");

        when(dataService.getAddressesByStationNumber(1)).thenReturn(Collections.singleton("123 Main St"));
        when(dataService.getPersonsByAddress("123 Main St")).thenReturn(Arrays.asList(personList.get(0), personList.get(1)));
        when(dataService.getMedicalRecordsByPersons(Arrays.asList(personList.get(0), personList.get(1)))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/flood/stations")
                        .param("stations", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['123 Main St']", hasSize(2)))
                .andExpect(jsonPath("$.['123 Main St'][0].age").value(0))
                .andExpect(jsonPath("$.['123 Main St'][0].medications", hasSize(0)))
                .andExpect(jsonPath("$.['123 Main St'][0].allergies", hasSize(0)));
    }
}
