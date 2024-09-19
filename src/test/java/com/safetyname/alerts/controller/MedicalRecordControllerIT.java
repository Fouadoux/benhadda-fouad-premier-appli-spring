package com.safetyname.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.service.DataService;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for MedicalRecordController.
 * This class tests CRUD operations for medical records via the HTTP endpoints.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
public class MedicalRecordControllerIT {

    private static final Logger logger = LogManager.getLogger(MedicalRecordControllerIT.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataService dataService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Setup method to load test data before each test case.
     */
    @BeforeEach
    void setUp() {
        logger.info("Setting up test data.");
        dataService.readJsonFile();
        dataService.saveData();
    }

    /**
     * Test for successfully adding a new medical record.
     */
    @Test
    void testAddMedicalRecordSuccess() throws Exception {
        logger.info("Testing successful medical record addition.");
        List<String> medications1 = Arrays.asList("Aspirin", "Ibuprofen");
        List<String> allergies1 = Arrays.asList("Peanuts", "Pollen");
        MedicalRecord newMedicalRecord = new MedicalRecord("John", "Doe", "04/09/1989", allergies1, medications1);

        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMedicalRecord)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Medical record added successfully"));

        List<MedicalRecord> medicalRecordList = dataService.getMedicalRecords();
        assertEquals(24, medicalRecordList.size());
    }

    /**
     * Test for attempting to add a medical record that causes a conflict.
     */
    @Test
    void testAddMedicalRecordConflict() throws Exception {
        logger.info("Testing medical record addition with conflict.");
        List<String> medications1 = Arrays.asList("medial1");
        List<String> allergies1 = Arrays.asList("allergie1");
        MedicalRecord newMedicalRecord = new MedicalRecord("John", "Boyd", "03/06/1984", medications1, allergies1);

        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMedicalRecord)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Conflict in addMedicalRecord"));
    }

    /**
     * Test for attempting to add a medical record with invalid data.
     */
    @Test
    void testAddMedicalRecordInvalidData() throws Exception {
        logger.info("Testing medical record addition with invalid data.");
        List<String> medications1 = Arrays.asList("Aspirin", "Ibuprofen");
        List<String> allergies1 = Arrays.asList("Peanuts", "Pollen");
        MedicalRecord newMedicalRecord = new MedicalRecord("", "Doe", "04/09/1989", allergies1, medications1);

        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMedicalRecord)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request in addMedicalRecord"));
    }

    /**
     * Test for successfully updating an existing medical record.
     */
    @Test
    void testUpdateMedicalRecordSuccess() throws Exception {
        logger.info("Testing successful medical record update.");
        List<String> medications1 = Arrays.asList("medial1");
        List<String> allergies1 = Arrays.asList("allergie1");
        MedicalRecord newMedicalRecord = new MedicalRecord("John", "Boyd", "03/06/1984", medications1, allergies1);

        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMedicalRecord)))
                .andExpect(status().isOk())
                .andExpect(content().string("Medical record updated successfully"));

        List<MedicalRecord> medicalRecords = dataService.getMedicalRecords();
        for (MedicalRecord medicalRecord : medicalRecords) {
            if (medicalRecord.getFirstName().equals(newMedicalRecord.getFirstName())
                    && medicalRecord.getLastName().equals(newMedicalRecord.getLastName())) {
                assertEquals(medications1, newMedicalRecord.getMedications());
                assertEquals(allergies1, newMedicalRecord.getAllergies());
            }
        }
    }

    /**
     * Test for attempting to update a medical record that causes a conflict.
     */
    @Test
    void testUpdateMedicalRecordConflict() throws Exception {
        logger.info("Testing medical record update with conflict.");
        List<String> medications1 = Arrays.asList("aznol:350mg", "hydrapermazol:100mg");
        List<String> allergies1 = Arrays.asList("nillacilan");
        MedicalRecord newMedicalRecord = new MedicalRecord("John", "Boyd", "03/06/1984", medications1, allergies1);

        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMedicalRecord)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Conflict in update medical record"));
    }

    /**
     * Test for attempting to update a medical record that does not exist (not found).
     */
    @Test
    void testUpdateMedicalRecordNotFound() throws Exception {
        logger.info("Testing medical record update with not found scenario.");
        List<String> medications1 = Arrays.asList("medial1");
        List<String> allergies1 = Arrays.asList("allergie1");
        MedicalRecord newMedicalRecord = new MedicalRecord("Frank", "Boyd", "03/06/1984", medications1, allergies1);

        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMedicalRecord)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Medical record not found"));
    }

    /**
     * Test for attempting to update a medical record with invalid data.
     */
    @Test
    void testUpdateMedicalRecordInvalidData() throws Exception {
        logger.info("Testing medical record update with invalid data.");
        List<String> medications1 = Arrays.asList("medial1");
        List<String> allergies1 = Arrays.asList("allergie1");
        MedicalRecord newMedicalRecord = new MedicalRecord(" ", "Boyd", "03/06/1984", medications1, allergies1);

        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMedicalRecord)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request"));
    }

    /**
     * Test for successfully deleting a medical record.
     */
    @Test
    void testDeleteMedicalRecordSuccess() throws Exception {
        logger.info("Testing successful medical record deletion.");
        mockMvc.perform(delete("/medicalRecord")
                        .param("firstName", "John")
                        .param("lastName", "Boyd"))
                .andExpect(status().isOk())
                .andExpect(content().string("Medical record deleted successfully"));

        List<MedicalRecord> medicalRecords = dataService.getMedicalRecords();
        assertEquals(22, medicalRecords.size());
    }

    /**
     * Test for attempting to delete a medical record that does not exist (not found).
     */
    @Test
    void testDeleteMedicalRecordNotFound() throws Exception {
        logger.info("Testing medical record deletion with not found scenario.");
        mockMvc.perform(delete("/medicalRecord")
                        .param("firstName", "Frank")
                        .param("lastName", "Boyd"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Medical record not found"));
    }

    /**
     * Test for attempting to delete a medical record with missing or invalid data (bad request).
     */
    @Test
    void testDeleteMedicalRecordNoContent() throws Exception {
        logger.info("Testing medical record deletion with missing or invalid data.");
        mockMvc.perform(delete("/medicalRecord")
                        .param("firstName", " ")
                        .param("lastName", "Boyd"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request"));
    }
}
