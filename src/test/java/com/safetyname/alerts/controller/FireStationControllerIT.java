package com.safetyname.alerts.controller;

import com.safetyname.alerts.entity.FireStation;
import com.safetyname.alerts.service.DataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetyname.alerts.service.IDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test class for the FireStationController.
 * This class tests various CRUD operations for fire station through the HTTP endpoints.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
public class FireStationControllerIT {

    private static final Logger logger = LogManager.getLogger(FireStationControllerIT.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IDataService dataService;

    private final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * Sets up the test environment before each test by reading and saving data.
     */
    @BeforeEach
    void setUp() {
        logger.info("Setting up test data.");
        dataService.readJsonFile();
        dataService.saveData();
    }

    /**
     * Tests the successful addition of a new fire station.
     */
    @Test
    void testAddFireStationSuccess() throws Exception {
        logger.info("Testing successful fire station addition.");
        FireStation newFireStation = new FireStation("29 test street", 2);

        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFireStation)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Fire Station added successfully"));

        List<FireStation> fireStationList = dataService.getFireStations();
        assertEquals(14, fireStationList.size());
    }

    /**
     * Tests adding a fire station that already exists, which should return a conflict.
     */
    @Test
    void testAddFireStationConflict() throws Exception {
        logger.info("Testing fire station addition with conflict.");
        FireStation newFireStation = new FireStation("947 E. Rose Dr", 2);

        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFireStation)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Conflict"));
    }

    /**
     * Tests adding a fire station with invalid data, which should return a bad request.
     */
    @Test
    void testAddFireStationInvalidData() throws Exception {
        logger.info("Testing fire station addition with invalid data.");
        FireStation newFireStation = new FireStation(" ", 2);

        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFireStation)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request"));
    }

    /**
     * Tests the successful update of an existing fire station.
     */
    @Test
    void testUpdateFirestationSuccess() throws Exception {
        logger.info("Testing successful fire station update.");
        FireStation fireStation = new FireStation("951 LoneTree Rd", 5);

        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isOk())
                .andExpect(content().string("Fire station updated successfully"));

        int result = dataService.getStationByAddress("951 LoneTree Rd");
        assertEquals(5, result);
    }

    /**
     * Tests updating a fire station where there is a conflict.
     */
    @Test
    void testUpdateFirestationConflict() throws Exception {
        logger.info("Testing fire station update with conflict.");
        FireStation fireStation = new FireStation("951 LoneTree Rd", 2);

        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Conflict: Fire station already exists with the same address and station"));
    }

    /**
     * Tests updating a fire station with invalid data, which should return a bad request.
     */
    @Test
    void testUpdateFirestationInvalidData() throws Exception {
        logger.info("Testing fire station update with invalid data.");
        FireStation fireStation = new FireStation("", 2);

        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request"));
    }

    /**
     * Tests updating a fire station with invalid address, which should return a not found.
     */
    @Test
    void testUpdateFirestationNotFound() throws Exception {
        logger.info("Testing fire station update with not found address.");
        FireStation fireStation = new FireStation("23 test St", 2);

        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Fire station address not found"));
    }

    /**
     * Tests the successful deletion of a fire station.
     */
    @Test
    void testDeleteFireStationSuccess() throws Exception {
        logger.info("Testing successful fire station deletion.");
        mockMvc.perform(delete("/firestation")
                        .param("address", "951 LoneTree Rd"))
                .andExpect(status().isOk())
                .andExpect(content().string("Fire station deleted successfully"));

        List<FireStation> fireStationList = dataService.getFireStations();
        assertEquals(12, fireStationList.size());
    }

    /**
     * Tests deleting a fire station with a non-existent address, which should return not found.
     */
    @Test
    void testDeleteFireStationWrongAddress() throws Exception {
        logger.info("Testing fire station deletion with wrong address.");
        mockMvc.perform(delete("/firestation")
                        .param("address", "951 test Rd"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Address not found"));
    }

    /**
     * Tests deleting a fire station with an empty address, which should return a bad request.
     */
    @Test
    void testDeleteFireStationNoContent() throws Exception {
        logger.info("Testing fire station deletion with no address provided.");
        mockMvc.perform(delete("/firestation")
                        .param("address", ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request"));
    }
}
