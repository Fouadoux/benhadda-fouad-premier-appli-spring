package com.safetyname.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetyname.alerts.service.IChildAlertService;
import com.safetyname.alerts.dto.ChildResponse; // Assuming you have this class
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link ChildAlertController}.
 * <p>
 * This class contains unit tests for the ChildAlertController endpoints, verifying
 * different scenarios such as when no persons are found, when children and family members are found,
 * and when no children are found at a given address.
 */
@WebMvcTest(ChildAlertController.class)
class ChildAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IChildAlertService childAlertService;


    private List<ChildResponse> children;
    private List<ChildResponse> noChildren;

    /**
     * Sets up the test data before each test method.
     */
    @BeforeEach
    void setUp() {
        // Initialize mock children response
        children = Arrays.asList(
                new ChildResponse("John", "Doe", 14, Arrays.asList("Jane Doe"))
        );

        noChildren = Collections.emptyList();
    }

    /**
     * Tests the scenario where no children are found at the given address.
     * <p>
     * Expects a 404 Not Found status when no children are returned by the ChildAlertService.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetChildNoPersonFound() throws Exception {
        String address = "123 Main St";
        // Simulate no children found at the address
        when(childAlertService.getChildrenByAddress(address)).thenReturn(noChildren);

        mockMvc.perform(get("/childAlert").param("address", address))
                .andExpect(status().isNotFound());  // Verifies that the status is 404 Not Found
    }

    /**
     * Tests the scenario where children and family members are found at the given address.
     * <p>
     * Expects a 200 OK status and verifies the content of the response.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetChildChildAndFamilyFound() throws Exception {
        String address = "123 Main St";

        // Simulate children and family members found
        when(childAlertService.getChildrenByAddress(address)).thenReturn(children);

        mockMvc.perform(get("/childAlert").param("address", address))
                .andExpect(status().isOk())  // Verifies that the status is 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))  // Verifies that there is one child
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].age").value(14))  // Verifies the age
                .andExpect(jsonPath("$[0].family", hasSize(1)))  // Verifies that there is one family member
                .andExpect(jsonPath("$[0].family[0]").value("Jane Doe"));
    }

    /**
     * Tests the scenario where no children are found at the given address.
     * <p>
     * Expects a 404 Not Found status when only adults are present at the address.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testGetChildNoChildFound() throws Exception {
        String address = "123 Main St";

        // Simulate no children found at the address
        when(childAlertService.getChildrenByAddress(address)).thenReturn(noChildren);

        mockMvc.perform(get("/childAlert").param("address", address))
                .andExpect(status().isNotFound());  // Verifies that the status is 404 Not Found
    }
}
