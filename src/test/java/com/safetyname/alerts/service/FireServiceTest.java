package com.safetyname.alerts.service;

import com.safetyname.alerts.dto.FireInfo;
import com.safetyname.alerts.dto.FireResponse;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
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

class FireServiceTest {

    @Mock
    private IDataService dataService;

    @InjectMocks
    private FireService fireService;

    private List<Person> persons;
    private List<MedicalRecord> medicalRecords;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        persons = Arrays.asList(
                new Person("John", "Doe", "123 Main St", "City", "john@example.com", 71100, "123-456-7890"),
                new Person("Jane", "Doe", "123 Main St", "City", "jane@example.com", 71100, "987-654-3210")
        );

        medicalRecords = Arrays.asList(
                new MedicalRecord("John", "Doe", "01/01/1985", Arrays.asList("Aspirin"), Arrays.asList("Peanuts")),
                new MedicalRecord("Jane", "Doe", "01/01/1990", Arrays.asList("Paracetamol"), Arrays.asList("Cats"))
        );
    }

    /**
     * Test the scenario where persons and medical records are found at the specified address.
     * Expects a valid FireResponse to be returned with the correct station and health info.
     */
    @Test
    void testGetFireServiceSuccess() {
        String address = "123 Main St";
        int fireStation = 1;

        // Mock the dataService methods
        when(dataService.getPersonsByAddress(address)).thenReturn(persons);
        when(dataService.getMedicalRecordsByPersons(persons)).thenReturn(medicalRecords);
        when(dataService.getStationByAddress(address)).thenReturn(fireStation);

        // Call the service method
        FireResponse fireResponse = fireService.getFireService(address);

        // Verify the result
        assertNotNull(fireResponse);
        assertEquals(fireStation, fireResponse.getStation());
        assertEquals(2, fireResponse.getFireInfos().size()); // Two people in the address

        // Verify the first person's information
        FireInfo johnInfo = fireResponse.getFireInfos().get(0);
        assertEquals("Doe", johnInfo.getLastName());
        assertEquals("123-456-7890", johnInfo.getPhone());
        assertEquals(39, johnInfo.getAge()); // Assuming calculateAge method works correctly
        assertTrue(johnInfo.getMedications().contains("Aspirin"));
        assertTrue(johnInfo.getAllergies().contains("Peanuts"));

        // Verify the second person's information
        FireInfo janeInfo = fireResponse.getFireInfos().get(1);
        assertEquals("Doe", janeInfo.getLastName());
        assertEquals("987-654-3210", janeInfo.getPhone());
        assertEquals(34, janeInfo.getAge()); // Assuming calculateAge method works correctly
        assertTrue(janeInfo.getMedications().contains("Paracetamol"));
        assertTrue(janeInfo.getAllergies().contains("Cats"));
    }

    /**
     * Test the scenario where no persons are found at the specified address.
     * Expects a FireResponse with an empty list of fireInfos.
     */
    @Test
    void testGetFireServiceNoPersonsFound() {
        String address = "123 Main St";
        int fireStation = 1;

        // Mock the dataService methods to return empty lists
        when(dataService.getPersonsByAddress(address)).thenReturn(Collections.emptyList());
        when(dataService.getStationByAddress(address)).thenReturn(fireStation);

        // Call the service method
        FireResponse fireResponse = fireService.getFireService(address);

        // Verify the result
        assertNotNull(fireResponse);
        assertEquals(fireStation, fireResponse.getStation());
        assertTrue(fireResponse.getFireInfos().isEmpty()); // No persons found, so fireInfos should be empty
    }

    /**
     * Test the scenario where no medical records are found for persons at the specified address.
     * Expects a FireResponse with empty health information for each person.
     */
    @Test
    void testGetFireServiceNoMedicalRecordsFound() {
        String address = "123 Main St";
        int fireStation = 1;

        // Mock the dataService methods
        when(dataService.getPersonsByAddress(address)).thenReturn(persons);
        when(dataService.getMedicalRecordsByPersons(persons)).thenReturn(Collections.emptyList());
        when(dataService.getStationByAddress(address)).thenReturn(fireStation);

        // Call the service method
        FireResponse fireResponse = fireService.getFireService(address);

        // Verify the result
        assertNotNull(fireResponse);
        assertEquals(fireStation, fireResponse.getStation());
        assertEquals(0, fireResponse.getFireInfos().size()); // Two people, but no medical info

        // Verify that medical info is missing (empty medications and allergies)
        assertTrue(fireResponse.getFireInfos().isEmpty());
    }
}
