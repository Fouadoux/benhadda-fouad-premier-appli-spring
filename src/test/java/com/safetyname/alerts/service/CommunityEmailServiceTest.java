package com.safetyname.alerts.service;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.CommunityEmailService;
import com.safetyname.alerts.service.IDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CommunityEmailServiceTest {

    @Mock
    private IDataService dataService;

    @InjectMocks
    private CommunityEmailService communityEmailService;

    private List<Person> persons;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        persons = Arrays.asList(
                new Person("John", "Doe", "123 Main St", "Springfield", "john@example.com", 71100, "123-456-7890"),
                new Person("Jane", "Doe", "456 Elm St", "Springfield", "jane@example.com", 71100, "987-654-3210"),
                new Person("Bob", "Smith", "789 Pine St", "AnotherCity", "bob@example.com", 71100, "555-555-5555")
        );
    }

    /**
     * Test the scenario where persons are found in the specified city and their emails are returned.
     */
    @Test
    void testGetEmailsByCitySuccess() {
        // Mock the dataService to return the list of persons
        when(dataService.getPersons()).thenReturn(persons);

        // Call the service method
        List<String> emails = communityEmailService.getEmailByCity("Springfield");

        // Verify that emails are returned
        assertFalse(emails.isEmpty());
        assertEquals(2, emails.size());  // Two people in Springfield
        assertTrue(emails.contains("john@example.com"));
        assertTrue(emails.contains("jane@example.com"));
    }

    /**
     * Test the scenario where no persons are found in the specified city.
     */
    @Test
    void testGetEmailsByCityNoEmailsFound() {
        // Mock the dataService to return the list of persons
        when(dataService.getPersons()).thenReturn(persons);

        // Call the service method with a city where no persons are found
        List<String> emails = communityEmailService.getEmailByCity("NonExistentCity");

        // Verify that no emails are returned
        assertTrue(emails.isEmpty());
    }

    /**
     * Test the scenario where no persons are found at all (empty list).
     */
    @Test
    void testGetEmailsByCityNoPersonsFound() {
        // Mock the dataService to return an empty list of persons
        when(dataService.getPersons()).thenReturn(Collections.emptyList());

        // Call the service method
        List<String> emails = communityEmailService.getEmailByCity("Springfield");

        // Verify that no emails are returned
        assertTrue(emails.isEmpty());
    }
}
