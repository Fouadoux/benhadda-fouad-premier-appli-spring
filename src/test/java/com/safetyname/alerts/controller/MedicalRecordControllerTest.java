package com.safetyname.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.service.DataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Test class for {@link MedicalRecordController}.
 * <p>
 * This class contains unit tests for the MedicalRecordController endpoints,
 * verifying different scenarios such as successful updates, additions,
 * deletions, and handling of invalid data.
 */
@WebMvcTest(MedicalRecordController.class)
class MedicalRecordControllerTest {

    private static final Logger logger = LogManager.getLogger(MedicalRecordControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataService dataService;

    private ObjectMapper objectMapper = new ObjectMapper();
    private List<MedicalRecord> medicalRecords;

    /**
     * Sets up the test data before each test method.
     * <p>
     * Initializes the list of medical records to be used in the tests.
     */
    @BeforeEach
    void setUp() {
        medicalRecords = new ArrayList<>();
        List<String> medications1 = Arrays.asList("Aspirin", "Ibuprofen");
        List<String> allergies1 = Arrays.asList("Peanuts", "Pollen");
        List<String> medications2 = Arrays.asList("Paracetamol", "Antibiotics");
        List<String> allergies2 = Arrays.asList("Dust", "Cats");
        medicalRecords.add(new MedicalRecord("Fouad", "Benhadda", "04/09/1989", allergies1, medications1));
        medicalRecords.add(new MedicalRecord("Pierre", "Benhadda", "25/12/2006", allergies2, medications2));
        when(dataService.getMedicalRecords()).thenReturn(medicalRecords);
    }

    /**
     * Tests the successful update of a medical record.
     * <p>
     * Expects a 200 OK status and verifies that the data service saves the data.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testUpdateMedicalRecordSuccess() throws Exception {
        logger.info("Testing successful update of a medical record.");
        List<String> medications = Arrays.asList("Aspirin", "Ibuprofen", "Magnesium");
        List<String> allergies = Arrays.asList("Pollen");
        MedicalRecord updatedMedicalRecord = new MedicalRecord("Fouad", "Benhadda", "04/09/1989", allergies, medications);
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMedicalRecord)))
                .andExpect(status().isOk())
                .andExpect(content().string("Medical record updated successfully"));

        verify(dataService).saveData();
    }

    /**
     * Tests updating a medical record that does not exist.
     * <p>
     * Expects a 404 Not Found status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testUpdateMedicalRecordFail() throws Exception {
        logger.info("Testing update of a non-existing medical record.");
        List<String> medications = Arrays.asList("Aspirin", "Ibuprofen", "Magnesium");
        List<String> allergies = Arrays.asList("Pollen");
        MedicalRecord invalidMedicalRecord = new MedicalRecord("Fuad", "Benhadda", "04/09/1989", allergies, medications);
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMedicalRecord)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Medical record not found"));
    }

    /**
     * Tests updating a medical record with invalid data.
     * <p>
     * Expects a 400 Bad Request status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testUpdateMedicalRecordInvalidData() throws Exception {
        logger.info("Testing update of a medical record with invalid data.");
        List<String> medications = Arrays.asList("Aspirin", "Ibuprofen", "Magnesium");
        List<String> allergies = Arrays.asList("Pollen");
        MedicalRecord medicalRecord = new MedicalRecord("", "", "04/09/1989", allergies, medications);

        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecord)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests updating a medical record with no content.
     * <p>
     * Expects a 400 Bad Request status due to missing request body.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testUpdateMedicalRecordNoContent() throws Exception {
        logger.info("Testing update of a medical record with no content.");
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests the successful addition of a new medical record.
     * <p>
     * Expects a 201 Created status and verifies that the data service saves the data.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testAddMedicalRecordSuccess() throws Exception {
        logger.info("Testing successful addition of a new medical record.");
        List<String> medications = Arrays.asList("Aspirin", "Ibuprofen", "Magnesium");
        List<String> allergies = Arrays.asList("Pollen");
        MedicalRecord medicalRecord = new MedicalRecord("Fred", "Michel", "04/09/1967", allergies, medications);
        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecord)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Medical record added successfully"));

        verify(dataService).saveData();
    }

    /**
     * Tests adding a medical record that already exists.
     * <p>
     * Expects a 409 Conflict status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testAddMedicalRecordConflict() throws Exception {
        logger.info("Testing addition of a medical record that already exists.");
        List<String> medications = Arrays.asList("Aspirin", "Ibuprofen");
        List<String> allergies = Arrays.asList("Peanuts", "Pollen");
        MedicalRecord medicalRecord = new MedicalRecord("Fouad", "Benhadda", "04/09/1989", allergies, medications);
        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecord)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Conflict in addMedicalRecord"));
    }

    /**
     * Tests the successful deletion of a medical record.
     * <p>
     * Expects a 200 OK status and verifies that the data service saves the data.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testDeleteMedicalRecordSuccess() throws Exception {
        logger.info("Testing successful deletion of a medical record.");
        mockMvc.perform(delete("/medicalRecord")
                        .param("firstName", "Fouad")
                        .param("lastName", "Benhadda"))
                .andExpect(status().isOk())
                .andExpect(content().string("Medical record deleted successfully"));

        verify(dataService).saveData();
    }

    /**
     * Tests deleting a medical record that does not exist.
     * <p>
     * Expects a 404 Not Found status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testDeleteMedicalRecord_NotFound() throws Exception {
        logger.info("Testing deletion of a non-existing medical record.");
        mockMvc.perform(delete("/medicalRecord")
                        .param("firstName", "John")
                        .param("lastName", "Benhadda"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Medical record not found"));
    }
}
