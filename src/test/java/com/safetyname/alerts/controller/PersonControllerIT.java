package com.safetyname.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.DataService;
import com.safetyname.alerts.service.IDataService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the PersonController.
 * This class tests various CRUD operations for persons through the HTTP endpoints.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
public class PersonControllerIT {

    private static final Logger logger = LogManager.getLogger(PersonControllerIT.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IDataService dataService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Set up before each test: loading and saving data.
     */
    @BeforeEach
    void setUp() {
        logger.info("Setting up test data.");
        dataService.readJsonFile();
        dataService.saveData();
    }

    /**
     * Test for successfully adding a person.
     */
    @Test
    void testAddPersonSuccess() throws Exception {
        logger.info("Testing successful addition of a person.");
        Person newPerson = new Person("John", "Doe", "123 Main St", "City1", "john@example.com", 71100, "123-456-7890");

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPerson)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Person added successfully"));

        List<Person> personList = dataService.getPersons();
        assertEquals(24, personList.size());
    }

    /**
     * Test for adding a person that causes a conflict.
     */
    @Test
    void testAddPersonConflict() throws Exception {
        logger.info("Testing person addition with conflict.");
        Person newPerson = new Person("John", "Boyd", "1509 Culver St", "Culver", "jaboyd@email.com", 97451, "841-874-6512");

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPerson)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Conflict in addPerson"));
    }

    /**
     * Test for adding a person with invalid data.
     */
    @Test
    void testAddPersonInvalidData() throws Exception {
        logger.info("Testing person addition with invalid data.");
        Person newPerson = new Person("", "Boyd", "1509 Culver St", "Culver", "jaboyd@email.com", 97451, "841-874-6512");

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPerson)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request"));
    }

    /**
     * Test for successfully updating a person.
     */
    @Test
    void testUpdatePersonSuccess() throws Exception {
        logger.info("Testing successful person update.");
        Person updatePerson = new Person("John", "Boyd", "1509 Culver St", "Culver", "jaboyd@email.com", 97451, "874-6512");

        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePerson)))
                .andExpect(status().isOk())
                .andExpect(content().string("Person updated successfully"));

        List<Person> personList = dataService.getPersons();
        for (Person person : personList) {
            if (person.getFirstName().equals(updatePerson.getFirstName())
                    && person.getLastName().equals(updatePerson.getLastName())) {
                assertEquals("874-6512", person.getPhone());
            }
        }
    }

    /**
     * Test for updating a person that causes a conflict.
     */
    @Test
    void testUpdatePersonConflict() throws Exception {
        logger.info("Testing person update with conflict.");
        Person newPerson = new Person("John", "Boyd", "1509 Culver St", "Culver", "jaboyd@email.com", 97451, "841-874-6512");

        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPerson)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Conflict in update Person"));
    }

    /**
     * Test for updating a person that does not exist (not found).
     */
    @Test
    void testUpdatePersonNotFound() throws Exception {
        logger.info("Testing person update with not found scenario.");
        Person newPerson = new Person("Frank", "Boyd", "1509 Culver St", "Culver", "jaboyd@email.com", 97451, "841-874-6512");

        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPerson)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Person not found in updatePerson"));
    }

    /**
     * Test for updating a person with invalid data.
     */
    @Test
    void testUpdatePersonInvalidData() throws Exception {
        logger.info("Testing person update with invalid data.");
        Person newPerson = new Person("", "Boyd", "1509 Culver St", "Culver", "jaboyd@email.com", 97451, "841-874-6512");

        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPerson)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request"));
    }

    /**
     * Test for successfully deleting a person.
     */
    @Test
    void testDeletePersonSuccess() throws Exception {
        logger.info("Testing successful person deletion.");
        mockMvc.perform(delete("/person")
                        .param("firstName", "John")
                        .param("lastName", "Boyd"))
                .andExpect(status().isOk())
                .andExpect(content().string("Person deleted successfully"));

        List<Person> personList = dataService.getPersons();
        assertEquals(22, personList.size());
    }

    /**
     * Test for deleting a person that does not exist (not found).
     */
    @Test
    void testDeletePersonNotFound() throws Exception {
        logger.info("Testing person deletion with not found scenario.");
        mockMvc.perform(delete("/person")
                        .param("firstName", "Frank")
                        .param("lastName", "Boyd"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Person not found"));
    }

    /**
     * Test for deleting a person with missing or invalid data (bad request).
     */
    @Test
    void testDeletePersonNoContent() throws Exception {
        logger.info("Testing person deletion with missing data.");
        mockMvc.perform(delete("/person")
                        .param("firstName", "")
                        .param("lastName", "Boyd"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request in deletePerson"));
    }
}
