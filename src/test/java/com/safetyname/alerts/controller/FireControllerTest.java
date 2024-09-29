package com.safetyname.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetyname.alerts.dto.FireResponse;
import com.safetyname.alerts.service.IFireService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Collections;

@WebMvcTest(FireController.class)
class FireControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IFireService fireService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    /**
     * Test the successful retrieval of fire information by address.
     * Expects a 200 OK status and verifies the returned data.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetFireInfoSuccess() throws Exception {
        // Mock the service method to return a valid FireResponse
        FireResponse fireResponse = new FireResponse(Collections.emptyList(), 1);
        when(fireService.getFireService(anyString())).thenReturn(fireResponse);

        // Perform the GET request
        mockMvc.perform(get("/fire").param("address", "123 Main St")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.station").value(1));
    }

    /**
     * Test the scenario where no fire information is found for the given address.
     * Expects a 404 Not Found status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetFireInfoNotFound() throws Exception {
        // Mock the service method to return null when no data is found
        when(fireService.getFireService(anyString())).thenReturn(null);

        // Perform the GET request
        mockMvc.perform(get("/fire").param("address", "123 NonExistent St")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Test the scenario where the address parameter is empty.
     * Expects a 400 Bad Request status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetFireInfoBadRequest() throws Exception {
        // Perform the GET request with an empty address
        mockMvc.perform(get("/fire").param("address", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
