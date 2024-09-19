package com.safetyname.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.DataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link PersonController}.
 * <p>
 * This class contains unit tests for the PersonController endpoints,
 * verifying different scenarios such as successful updates, additions,
 * deletions, and handling of invalid data.
 */
@WebMvcTest(PersonController.class)
class PersonControllerTest {

    private static final Logger logger = LogManager.getLogger(PersonControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataService dataService;

    private ObjectMapper objectMapper = new ObjectMapper();
    private List<Person> persons;

    /**
     * Sets up the test data before each test method.
     * <p>
     * Initializes the list of persons to be used in the tests.
     */
    @BeforeEach
    void setUp() {
        persons = new ArrayList<>();
        persons.add(new Person("Fouad", "Benhadda", "19 Pasteur Street",
                "Chalon-sur-Saone", "fouad@gmail.com", 71100, "0673648562"));
        when(dataService.getPersons()).thenReturn(persons);
    }

    /**
     * Tests the successful update of a person.
     * <p>
     * Expects a 200 OK status and verifies that the data service saves the data.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testUpdatePersonSuccess() throws Exception {
        logger.info("Testing successful update of a person.");
        Person updatedPerson = new Person("Fouad", "Benhadda", "21 Pasteur Street",
                "Paris", "fouad@gmail.com", 75007, "0673648562");
        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPerson)))
                .andExpect(status().isOk())
                .andExpect(content().string("Person updated successfully"));
        verify(dataService).saveData();
    }

    /**
     * Tests updating a person that does not exist.
     * <p>
     * Expects a 404 Not Found status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testUpdatePersonFail() throws Exception {
        logger.info("Testing update of a non-existing person.");
        Person updatedPerson = new Person("Fouad", "benhadda", "21 Pasteur Street",
                "Paris", "fouad@gmail.com", 75007, "0673648562");
        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPerson)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Person not found in updatePerson"));
    }

    /**
     * Tests updating a person with invalid data.
     * <p>
     * Expects a 400 Bad Request status due to missing first name and last name.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testUpdatePersonInvalidData() throws Exception {
        logger.info("Testing update of a person with invalid data.");
        Person invalidPerson = new Person("", "", "19 Pasteur Street", "Paris",
                "fouad@gmail.com", 75001, "0673648562");

        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPerson)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests updating a person with no content.
     * <p>
     * Expects a 400 Bad Request status due to missing request body.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testUpdatePersonNoContent() throws Exception {
        logger.info("Testing update of a person with no content.");
        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests the successful addition of a new person.
     * <p>
     * Expects a 201 Created status and verifies that the data service saves the data.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testAddPersonSuccess() throws Exception {
        logger.info("Testing successful addition of a new person.");
        Person newPerson = new Person("Pierre", "Dupont", "14 Tintin Street",
                "Brussels", "pierre.dupont@example.com", 1000, "0673648562");
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPerson)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Person added successfully"));

        verify(dataService).saveData();
    }

    /**
     * Tests adding a person with invalid data.
     * <p>
     * Expects a 400 Bad Request status due to missing required fields.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testAddPersonInvalidData() throws Exception {
        logger.info("Testing addition of a person with invalid data.");
        Person invalidPerson = new Person("", "", "", "", "", 0, "");
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPerson)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests adding a person that already exists.
     * <p>
     * Expects a 409 Conflict status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testAddPersonConflict() throws Exception {
        logger.info("Testing addition of a person that already exists.");
        Person existingPerson = new Person("Fouad", "Benhadda", "19 Pasteur Street",
                "Chalon-sur-Saone", "fouad@gmail.com", 71100, "0673648562");
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingPerson)))
                .andExpect(status().isConflict());
    }

    /**
     * Tests the successful deletion of a person.
     * <p>
     * Expects a 200 OK status and verifies that the data service saves the data.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testDeletePersonSuccess() throws Exception {
        logger.info("Testing successful deletion of a person.");
        mockMvc.perform(delete("/person")
                        .param("firstName", "Fouad")
                        .param("lastName", "Benhadda"))
                .andExpect(status().isOk())
                .andExpect(content().string("Person deleted successfully"));

        verify(dataService).saveData();
    }

    /**
     * Tests deleting a person that does not exist.
     * <p>
     * Expects a 404 Not Found status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testDeletePersonNotFound() throws Exception {
        logger.info("Testing deletion of a non-existing person.");
        mockMvc.perform(delete("/person")
                        .param("firstName", "Pierre")
                        .param("lastName", "Benhadda"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Person not found"));
    }
}
