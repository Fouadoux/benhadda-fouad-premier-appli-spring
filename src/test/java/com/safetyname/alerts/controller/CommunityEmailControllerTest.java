package com.safetyname.alerts.controller;

import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.DataService;
import com.safetyname.alerts.service.ICommunityEmailService;
import com.safetyname.alerts.service.IDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Test class for {@link CommunityEmailController}.
 * <p>
 * This class contains unit tests for the CommunityEmailController endpoints, verifying
 * different scenarios such as successful retrieval of emails by city, handling of bad requests,
 * and cases where the city is not found.
 */
@WebMvcTest(CommunityEmailController.class)
class CommunityEmailControllerTest {

    private static final Logger logger = LogManager.getLogger(CommunityEmailControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICommunityEmailService communityEmailService;

    private List<String> personEmails;

    /**
     * Sets up the test data before each test method.
     * <p>
     * Initializes the list of persons to be used in the tests.
     */
    @BeforeEach
    void setUp() {
        personEmails = Arrays.asList("john@example.com","jane@example.com");

    }

    /**
     * Tests the successful retrieval of emails by city.
     * <p>
     * Expects a 200 OK status and verifies that the correct emails are returned.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testRetrieveEmailsByCitySuccess() throws Exception {
        logger.info("Testing successful retrieval of emails for city: City1");
        String city= "city1";

        when(communityEmailService.getEmailByCity(city)).thenReturn(personEmails);

        mockMvc.perform(get("/communityEmail").param("city",city)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("john@example.com"))
                .andExpect(jsonPath("$[1]").value("jane@example.com"));
    }

    /**
     * Tests the handling of a bad request when the city parameter is empty.
     * <p>
     * Expects a 400 Bad Request status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testRetrieveEmailsByCityBadRequest() throws Exception {
        logger.info("Testing bad request handling for empty city parameter");

        mockMvc.perform(get("/communityEmail").param("city"," ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests the scenario where the specified city is not found.
     * <p>
     * Expects a 404 Not Found status.
     *
     * @throws Exception if an error occurs during the request.
     */
    @Test
    void testRetrieveEmailsByCityNotFound() throws Exception {
        logger.info("Testing handling of city not found: City2");
        String city= "city1";
        String city2= "city2";

        when(communityEmailService.getEmailByCity(city)).thenReturn(personEmails);


        mockMvc.perform(get("/communityEmail").param("city",city2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
