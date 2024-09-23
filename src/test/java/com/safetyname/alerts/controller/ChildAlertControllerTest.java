package com.safetyname.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link ChildAlertController}.
 * <p>
 * This class contains unit tests for the ChildAlertController endpoints, verifying
 * different scenarios such as when no persons are found, when children and family members are found,
 * and when no children are found at a given address.
 */
@WebMvcTest(ChildAlertController.class)
class ChildAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataService dataService;

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
                new MedicalRecord("John", "Doe", "01/01/2010", Arrays.asList("med1"), Arrays.asList("allergy1")),  // Child
                new MedicalRecord("Jane", "Doe", "01/01/1985", Arrays.asList("med2"), Arrays.asList("allergy2"))   // Adult
        );
    }

    /**
     * Tests the scenario where no persons are found at the given address.
     * <p>
     * Expects a 404 Not Found status when no persons are returned by the DataService.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetChildNoPersonFound() throws Exception {
        String address = "123 Main St";
        // Simulate no persons found at the address
        when(dataService.getPersonsByAddress(address)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/childAlert").param("address", address))
                .andExpect(status().isNotFound());  // Verifies that the status is 404 Not Found
    }

    /**
     * Tests the scenario where children and family members are found at the given address.
     * <p>
     * Expects a 200 OK status and verifies the content of the response.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetChildChildAndFamilyFound() throws Exception {
        String address = "123 Main St";

        // Simulate persons and medical records found
        when(dataService.getPersonsByAddress(address)).thenReturn(persons);
        when(dataService.getMedicalRecordsByPersons(persons)).thenReturn(medicalRecords);

        mockMvc.perform(get("/childAlert").param("address", address))
                .andExpect(status().isOk())  // Verifies that the status is 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))  // Verifies that there is one child
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].age").value(14))  // Calculated age
                .andExpect(jsonPath("$[0].family", hasSize(1)))  // Verifies that there is one family member
                .andExpect(jsonPath("$[0].family[0]").value("Jane Doe"));
    }

    /**
     * Tests the scenario where no children are found at the given address.
     * <p>
     * Expects a 404 Not Found status when only adults are present at the address.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetChildNoChildFound() throws Exception {
        String address = "123 Main St";

        // Simulate only adults at the address
        List<MedicalRecord> medicalRecordsNoChild = Arrays.asList(
                new MedicalRecord("Jane", "Doe", "01/01/1985", Arrays.asList("med2"), Arrays.asList("allergy2"))   // Adult
        );

        when(dataService.getPersonsByAddress(address)).thenReturn(persons);
        when(dataService.getMedicalRecordsByPersons(persons)).thenReturn(medicalRecordsNoChild);

        mockMvc.perform(get("/childAlert").param("address", address))
                .andExpect(status().isNotFound());  // Verifies that the status is 404 Not Found
    }
}
