package com.safetyname.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetyname.alerts.entity.FireStation;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FirestationController.class)
class FirestationControllerTest {


    @Autowired
    MockMvc mockMvc;

    @MockBean
    DataService dataService;

    ObjectMapper objectMapper = new ObjectMapper();
    List<FireStation> fireStations;

    @BeforeEach
    void setUp() {
        fireStations=new ArrayList<>();
        fireStations.add(new FireStation("19 rue pasteur",4));
        when(dataService.getFireStations()).thenReturn(fireStations);
    }

    @Test
    void testUpdateFirestation_Success() throws Exception {
        FireStation fireStation = new FireStation("19 rue pasteur",5);
        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isOk())
                .andExpect(content().string("Fire station updated successfully"));
        verify(dataService).saveData(anyString());
    }

    @Test
    void testUpdateFirestationTest_Fail() throws Exception{
        FireStation fireStation = new FireStation("105 avenue des champs",4);
        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Fire station address not found"));
    }

    @Test
    public void testUpdateFirestation_InvalidData() throws Exception {
        FireStation fireStation = new FireStation("",5);

        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateFirestation_NoContent() throws Exception{

        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddFirestation_success() throws Exception {
        FireStation fireStation = new FireStation("34 rue de beaune",2);
        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Fire Station added successfully"));

        verify(dataService).saveData(anyString());
    }

    @Test
    void testAddFirestation_InvalidData()throws Exception{
        FireStation fireStation = new FireStation("",4);
        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isBadRequest());

    }
    @Test
    void testFirestation_Conflit()throws Exception{
        FireStation fireStation = new FireStation("19 rue pasteur",4);
        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStation)))
                .andExpect(status().isConflict());

    }

    @Test
    public void testDeleteFirestation_Success() throws Exception {

        mockMvc.perform(delete("/firestation")
                        .param("address","19 rue pasteur" ))
                .andExpect(status().isOk())
                .andExpect(content().string("Fire station deleted succesfully"));

        verify(dataService).saveData(anyString());
    }
    @Test
    public void testDeleteFirestation_NotFound() throws Exception {

        mockMvc.perform(delete("/firestation")
                        .param("address", "19 rue pastis"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Address not found"));
    }


}