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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MedicalRecordController.class)
class MedicalRecordControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    DataService dataService;

    ObjectMapper objectMapper = new ObjectMapper();
    List<MedicalRecord> medicalRecords;

    @BeforeEach
    void setUp(){
        medicalRecords=new ArrayList<>();
        List<String> medications1 = Arrays.asList("Aspirin", "Ibuprofen");
        List<String> allergies1 = Arrays.asList("Peanuts", "Pollen");
        List<String> medications2 = Arrays.asList("Paracetamol", "Antibiotics");
        List<String> allergies2 = Arrays.asList("Dust", "Cats");
        medicalRecords.add(new MedicalRecord("Fouad","Benhadda","04/09/1989",allergies1,medications1));
        medicalRecords.add(new MedicalRecord("Pierre","Benhadda","25/12/2006",allergies2,medications2));
        when(dataService.getMedicalRecords()).thenReturn(medicalRecords);
    }

    @Test
    void UpdateMedicalRecord_Success() throws Exception{
        List<String> medications = Arrays.asList("Aspirin", "Ibuprofen","magnesium");
        List<String> allergies = Arrays.asList("Pollen");
        MedicalRecord UpdateMedicalRecord = new MedicalRecord("Fouad","Benhadda","04/09/1989",allergies,medications);
        mockMvc.perform(put("/medicalRecord")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(UpdateMedicalRecord)))
                .andExpect(status().isOk())
                .andExpect(content().string("Medical record updated successfully"));

        verify(dataService).saveData(anyString());
    }

    @Test
    void UpdateMedicalRecord_fail() throws Exception{
        List<String> medications = Arrays.asList("Aspirin", "Ibuprofen","magnesium");
        List<String> allergies = Arrays.asList("Pollen");
        MedicalRecord invalidMedicalRecord = new MedicalRecord("Fuad","Benhadda","04/09/1989",allergies,medications);
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMedicalRecord)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Medical record not found"));
    }

    @Test
    public void testUpdateMedicalRecord_InvalidData() throws Exception {
        List<String> medications = Arrays.asList("Aspirin", "Ibuprofen","magnesium");
        List<String> allergies = Arrays.asList("Pollen");
        MedicalRecord medicalRecord = new MedicalRecord("","","04/09/1989",allergies,medications);


        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecord)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void UpdateMedicalRecord_NoContent() throws Exception{
         mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString("")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddMedicalRecord_Success() throws Exception{
        List<String> medications = Arrays.asList("Aspirin", "Ibuprofen","magnesium");
        List<String> allergies = Arrays.asList("Pollen");
        MedicalRecord medicalRecord = new MedicalRecord("Fred","michel","04/09/1967",allergies,medications);
        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecord)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Medical record added successfully"));

        verify(dataService).saveData(anyString());
    }

    @Test
    void testAddMedicalRecord_Conflit() throws Exception{
        List<String> medications1 = Arrays.asList("Aspirin", "Ibuprofen");
        List<String> allergies1 = Arrays.asList("Peanuts", "Pollen");
        MedicalRecord medicalRecord = new MedicalRecord("Fouad","Benhadda","04/09/1989",allergies1,medications1);
        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecord)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Conflit"));
    }

    @Test
    public void testDeletePerson_Success() throws Exception {

        mockMvc.perform(delete("/medicalRecord")
                        .param("firstName", "Fouad")
                        .param("lastName", "Benhadda"))
                .andExpect(status().isOk())
                .andExpect(content().string("Medical record deleted successfully"));

        verify(dataService).saveData(anyString());
    }
    @Test
    public void testDeletePerson_NotFound() throws Exception {

        mockMvc.perform(delete("/medicalRecord")
                        .param("firstName", "john")
                        .param("lastName", "Benhadda"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Medical record not found"));
    }













}