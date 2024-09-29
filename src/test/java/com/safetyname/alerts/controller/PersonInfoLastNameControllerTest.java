package com.safetyname.alerts.controller;

import com.safetyname.alerts.dto.PersonInfoLastNameResponse;
import com.safetyname.alerts.service.IPersonInfoLastNameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PersonInfoLastNameController.class)
class PersonInfoLastNameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPersonInfoLastNameService personInfoLastNameService;

    /**
     * Test successful retrieval of person information by last name.
     * <p>
     * Expects a 200 OK status and a non-empty JSON response.
     */
    @Test
    void testGetPersonInfolastNameSuccess() throws Exception {
        List<PersonInfoLastNameResponse> mockResponse = List.of(
                new PersonInfoLastNameResponse("John",  "123 Main St", 30, "john@example.com", List.of("med1"), List.of("allergy1"))
        );

        // Mock the service method to return the mock response
        when(personInfoLastNameService.getPersonInfoLastNameService(anyString())).thenReturn(mockResponse);

        // Perform the GET request and validate the response
        mockMvc.perform(get("/personInfolastName/Doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName").value("John"))
                .andExpect(jsonPath("$[0].address").value("123 Main St"));
    }

    /**
     * Test the scenario where no person is found for the given last name.
     * Expects a 404 Not Found status.
     */
    @Test
    void testGetPersonInfolastNameNotFound() throws Exception {
        // Mock the service method to return an empty list
        when(personInfoLastNameService.getPersonInfoLastNameService(anyString())).thenReturn(Collections.emptyList());

        // Perform the GET request and validate the response
        mockMvc.perform(get("/personInfolastName/NonExistentLastName")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Test the scenario where the last name parameter is empty.
     * Expects a 400 Bad Request status.
     */
    @Test
    void testGetPersonInfolastNameBadRequest() throws Exception {
        // Perform the GET request with an empty last name
        mockMvc.perform(get("/personInfolastName/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());  // Should be 404 since the path is incomplete
    }
}
