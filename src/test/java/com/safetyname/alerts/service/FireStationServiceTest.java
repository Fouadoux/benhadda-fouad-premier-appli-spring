package com.safetyname.alerts.service;

import com.safetyname.alerts.dto.FirestationResponse;
import com.safetyname.alerts.dto.PersonInfo;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

class FireStationServiceTest {

    private static final Logger logger = LogManager.getLogger(FireStationServiceTest.class);

    @Mock
    private IDataService dataService;

    @InjectMocks
    private FireStationService fireStationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPersonsCoveredByFirestationSuccess() {
        logger.info("Testing retrieval of persons covered by a fire station.");
        // Simulate data returned by DataService for persons
        List<Person> personsCovered = Arrays.asList(
                new Person("John", "Doe", "123 Main St", "City1", "john@example.com", 71100, "123-456-7890"),
                new Person("Jane", "Smith", "456 Oak St", "City2", "jane@example.com", 71100, "987-654-3210")
        );
        when(dataService.getPersonsByStationNumber(1)).thenReturn(personsCovered);

        // Simulate medical records associated with the persons
        List<MedicalRecord> medicalRecords = Arrays.asList(
                new MedicalRecord("John", "Doe", "01/01/1980", Arrays.asList("med1"), Arrays.asList("allergy1")),
                new MedicalRecord("Jane", "Smith", "01/01/2015", Arrays.asList("med2"), Arrays.asList("allergy2"))
        );
        when(dataService.getMedicalRecordsByPersons(personsCovered)).thenReturn(medicalRecords);

        // Call the service method
        FirestationResponse response = fireStationService.getFireStationService(1);

        // Verify the response
        assertNotNull(response);
        assertEquals(2, response.getPersons().size());  // Verify the number of persons
        assertEquals(1, response.getChildCount());  // Verify the child count
        assertEquals(1, response.getAdultCount());  // Verify the adult count

        // Verify PersonInfo objects
        PersonInfo person1 = response.getPersons().get(0);
        PersonInfo person2 = response.getPersons().get(1);

        assertEquals("John", person1.getFirstName());
        assertEquals("Doe", person1.getLastName());
        assertEquals("123 Main St", person1.getAddress());
        assertEquals("123-456-7890", person1.getPhone());

        assertEquals("Jane", person2.getFirstName());
        assertEquals("Smith", person2.getLastName());
        assertEquals("456 Oak St", person2.getAddress());
        assertEquals("987-654-3210", person2.getPhone());
    }

    @Test
    void testGetPersonsCoveredByFirestationNoPersonsFound() {
        logger.info("Testing retrieval of persons when no one is found for the fire station.");
        // Simulate no persons found for the fire station
        when(dataService.getPersonsByStationNumber(1)).thenReturn(Collections.emptyList());

        // Call the service method
        FirestationResponse response = fireStationService.getFireStationService(1);

        // Verify the response is null since no persons are found
        assertNull(response);
    }

    @Test
    void testGetPersonsCoveredByFirestationNoMedicalRecordsFound() {
        logger.info("Testing retrieval when no medical records are found for the persons.");
        // Simulate persons found
        List<Person> personsCovered = Arrays.asList(
                new Person("John", "Doe", "123 Main St", "City1", "john@example.com", 71100, "123-456-7890")
        );
        when(dataService.getPersonsByStationNumber(1)).thenReturn(personsCovered);

        // Simulate no medical records found
        when(dataService.getMedicalRecordsByPersons(personsCovered)).thenReturn(Collections.emptyList());

        // Call the service method
        FirestationResponse response = fireStationService.getFireStationService(1);

        // Verify the response is null since no medical records are found
        assertNull(response);
    }
}
