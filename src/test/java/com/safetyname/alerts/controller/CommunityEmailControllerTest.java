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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(CommunityEmailController.class)
class CommunityEmailControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    DataService dataService;

    ObjectMapper objectMapper = new ObjectMapper();
    List<FireStation> fireStations;

    @BeforeEach
    void setUp() {
        List<Person> personsCovered = Arrays.asList(
                new Person("John", "Doe", "123 Main St", "City1", "john@example.com", 71100, "123-456-7890"),
                new Person("Jane", "Smith", "456 Oak St", "City1", "jane@example.com", 71100, "987-654-3210")
        );
        when(dataService.getPersons()).thenReturn(personsCovered);
    }

    @Test
    void getEmailByCity() throws Exception {

        mockMvc.perform(get("/communityEmail?city=city1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("john@example.com"))
                .andExpect(jsonPath("$[1]").value("jane@example.com"));
    }
    @Test
    void getEmailByCity_fail() throws Exception {

        mockMvc.perform(get("/communityEmail?city=")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getEmailByCity_cityNotFound() throws Exception{

        mockMvc.perform(get("/communityEmail?city=city2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}