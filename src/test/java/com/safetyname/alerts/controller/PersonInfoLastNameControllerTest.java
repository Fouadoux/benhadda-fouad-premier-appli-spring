package com.safetyname.alerts.controller;

import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.DataService;
import com.safetyname.alerts.service.IDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

// Import Logger classes
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Test class for {@link PersonInfoLastNameController}.
 * <p>
 * This class contains unit tests for the PersonInfoLastNameController endpoints,
 * verifying different scenarios such as when persons and medical records are found,
 * when the last name is empty, when medical records do not match, and when no persons
 * or medical records are found.
 */
@WebMvcTest(PersonInfoLastNameController.class)
class PersonInfoLastNameControllerTest {

    // Logger instance
    private static final Logger logger = LogManager.getLogger(PersonInfoLastNameControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IDataService dataService;

    /**
     * Tests the scenario where persons and medical records are found for a given last name.
     * Expects a 200 OK status and verifies the response content.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetPersonInfoLastNamePersonAndMedicalRecordFound() throws Exception {
        logger.info("Testing retrieval of person info by last name when persons and medical records are found.");

        List<Person> persons = Arrays.asList(
                new Person("John", "Doe", "123 Main St", "City1", "john@example.com", 71100, "123-456-7890"),
                new Person("Jane", "Doe", "456 Oak St", "City1", "jane@example.com", 71100, "987-654-3210")
        );

        List<MedicalRecord> medicalRecords = new ArrayList<>();
        List<String> medications1 = Arrays.asList("Aspirin", "Ibuprofen");
        List<String> allergies1 = Arrays.asList("Peanuts", "Pollen");
        List<String> medications2 = Arrays.asList("Paracetamol", "Antibiotics");
        List<String> allergies2 = Arrays.asList("Dust", "Cats");
        medicalRecords.add(new MedicalRecord("John", "Doe", "04/09/1989", allergies1, medications1));
        medicalRecords.add(new MedicalRecord("Jane", "Doe", "12/25/2006", allergies2, medications2));
        String lastName = "Doe";
        when(dataService.getPersonsByLastName(lastName)).thenReturn(persons);
        when(dataService.getMedicalRecordsByPersons(persons)).thenReturn(medicalRecords);

        mockMvc.perform(get("/personInfolastName/" + lastName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].address").value("123 Main St"))
                .andExpect(jsonPath("$[0].medications[0]").value("Aspirin"))
                .andExpect(jsonPath("$[0].allergies[0]").value("Peanuts"))
                .andExpect(jsonPath("$[1].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].address").value("456 Oak St"))
                .andExpect(jsonPath("$[1].medications[0]").value("Paracetamol"))
                .andExpect(jsonPath("$[1].allergies[0]").value("Dust"));

    }

    /**
     * Tests the scenario where the last name is empty.
     * Expects a 400 Bad Request status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetPersonInfoLastNameEmptyLastName() throws Exception {
        logger.info("Testing retrieval of person info by last name when last name is empty.");
        // Case where the last name is empty
        mockMvc.perform(get("/personInfolastName/ "))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests the scenario where persons are found but medical records do not match.
     * Expects a 404 Not Found status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetPersonInfoLastNamePersonAndMedicalRecordNoMatch() throws Exception {
        logger.info("Testing retrieval of person info by last name when medical records do not match.");

        String lastName = "Doe";
        List<String> medications2 = Arrays.asList("Paracetamol", "Antibiotics");
        List<String> allergies2 = Arrays.asList("Dust", "Cats");
        List<Person> personsA = Arrays.asList(new Person("John", "Doe", "123 Main St",
                "City1", "john@example.com", 71100, "123-456-7890"));
        List<MedicalRecord> medicalRecordsA = Arrays.asList(
                new MedicalRecord("Jane", "Smith", "25/12/2006", allergies2, medications2));

        when(dataService.getPersonsByLastName(lastName)).thenReturn(personsA);
        when(dataService.getMedicalRecordsByPersons(personsA)).thenReturn(medicalRecordsA);

        mockMvc.perform(get("/personInfolastName/" + lastName))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests the scenario where no medical records are found for the persons.
     * Expects a 404 Not Found status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetPersonInfoLastNameNoMedicalRecordsFound() throws Exception {
        logger.info("Testing retrieval of person info by last name when no medical records are found.");

        String lastName = "Doe";
        List<Person> persons = Arrays.asList(new Person("John", "Doe", "123 Main St",
                "City1", "john@example.com", 71100, "123-456-7890"));
        when(dataService.getPersonsByLastName(lastName)).thenReturn(persons);
        when(dataService.getMedicalRecordsByPersons(persons)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/personInfolastName/" + lastName))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests the scenario where no persons are found for the given last name.
     * Expects a 404 Not Found status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetPersonInfoLastNameNoPersonFound() throws Exception {
        logger.info("Testing retrieval of person info by last name when no persons are found.");

        String lastName = "Doe";
        when(dataService.getPersonsByLastName(lastName)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/personInfolastName/" + lastName))
                .andExpect(status().isNotFound());
    }

}
