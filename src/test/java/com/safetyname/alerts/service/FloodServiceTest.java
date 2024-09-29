package com.safetyname.alerts.service;

import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.dto.FloodResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class FloodServiceTest {

    @Mock
    private IDataService dataService;

    private FloodService floodService;

    private List<Person> personList;
    private List<MedicalRecord> medicalRecords;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        floodService = new FloodService(dataService); // Instantiate the FloodService with the mocked dataService

        // Initialize test data
        personList = Arrays.asList(
                new Person("John", "Doe", "123 Main St", "City", "john@example.com", 71100, "123-456-7890"),
                new Person("Jane", "Doe", "123 Main St", "City", "jane@example.com", 71100, "987-654-3210"),
                new Person("Bob", "Smith", "456 Elm St", "City", "bob@example.com", 71100, "555-555-5555")
        );

        medicalRecords = Arrays.asList(
                new MedicalRecord("John", "Doe", "01/01/1985", Arrays.asList("Aspirin"), Arrays.asList("Peanuts")),
                new MedicalRecord("Jane", "Doe", "01/01/1990", Arrays.asList("Paracetamol"), Arrays.asList("Cats")),
                new MedicalRecord("Bob", "Smith", "01/01/1980", Arrays.asList("Ibuprofen"), Arrays.asList("Pollen"))
        );
    }

    @Test
    void testGetHouseholdsByStationSuccess() {
        // Mock methods of DataService
        when(dataService.getAddressesByStationNumber(1)).thenReturn(Collections.singleton("123 Main St"));
        when(dataService.getAddressesByStationNumber(2)).thenReturn(Collections.singleton("456 Elm St"));
        when(dataService.getPersonsByAddress("123 Main St")).thenReturn(Arrays.asList(personList.get(0), personList.get(1)));
        when(dataService.getPersonsByAddress("456 Elm St")).thenReturn(Collections.singletonList(personList.get(2)));
        when(dataService.getMedicalRecordsByPersons(anyList())).thenReturn(medicalRecords);

        // Mock CalculateAgeService
        try (MockedStatic<CalculateAgeService> mockedStatic = mockStatic(CalculateAgeService.class)) {
            mockedStatic.when(() -> CalculateAgeService.calculateAge("01/01/1985")).thenReturn(38);
            mockedStatic.when(() -> CalculateAgeService.calculateAge("01/01/1990")).thenReturn(33);
            mockedStatic.when(() -> CalculateAgeService.calculateAge("01/01/1980")).thenReturn(43);

            // Call the service method
            Map<String, List<FloodResponse>> households = floodService.getFloodService(Arrays.asList(1, 2));

            // Basic assertions
            assertNotNull(households);
            assertTrue(households.containsKey("123 Main St"));
            assertTrue(households.containsKey("456 Elm St"));

            // Check size of each household
            assertEquals(2, households.get("123 Main St").size()); // 2 persons in 123 Main St
            assertEquals(1, households.get("456 Elm St").size()); // 1 person in 456 Elm St

            // Verify John Doe's data in the first household
            FloodResponse johnDoeResponse = households.get("123 Main St").get(0);
            assertEquals("Doe", johnDoeResponse.getLastName());
            assertEquals(38, johnDoeResponse.getAge());

            // Verify Bob Smith's data in the second household
            FloodResponse bobSmithResponse = households.get("456 Elm St").get(0);
            assertEquals("Smith", bobSmithResponse.getLastName());
            assertEquals(43, bobSmithResponse.getAge());
        }
    }
    @Test
    void testNoAddressesFoundForStation() {
        // Mock DataService to return empty sets for station numbers
        when(dataService.getAddressesByStationNumber(1)).thenReturn(Collections.emptySet());

        // Call the service method
        Map<String, List<FloodResponse>> households = floodService.getFloodService(Arrays.asList(1));

        // Assert that the result is empty
        assertTrue(households.isEmpty());
    }

    @Test
    void testNoPersonsFoundAtAddress() {
        // Mock DataService to return addresses but no persons
        when(dataService.getAddressesByStationNumber(1)).thenReturn(Collections.singleton("123 Main St"));
        when(dataService.getPersonsByAddress("123 Main St")).thenReturn(Collections.emptyList());

        // Call the service method
        Map<String, List<FloodResponse>> households = floodService.getFloodService(Arrays.asList(1));

        // Assert that the result is empty since no persons are found
        assertTrue(households.isEmpty());
    }

    @Test
    void testNoMedicalRecordsFoundForPersons() {
        // Mock DataService to return persons but no medical records
        when(dataService.getAddressesByStationNumber(1)).thenReturn(Collections.singleton("123 Main St"));
        when(dataService.getPersonsByAddress("123 Main St")).thenReturn(Arrays.asList(personList.get(0)));
        when(dataService.getMedicalRecordsByPersons(anyList())).thenReturn(Collections.emptyList());

        // Mock CalculateAgeService to return a default value
        try (MockedStatic<CalculateAgeService> mockedStatic = mockStatic(CalculateAgeService.class)) {
            mockedStatic.when(() -> CalculateAgeService.calculateAge(anyString())).thenReturn(-1);

            // Call the service method
            Map<String, List<FloodResponse>> households = floodService.getFloodService(Arrays.asList(1));

            // Assert that the household is not empty, but the medical information is missing
            assertFalse(households.isEmpty());
            FloodResponse johnDoeResponse = households.get("123 Main St").get(0);
            assertEquals("Doe", johnDoeResponse.getLastName());
            assertEquals(-1, johnDoeResponse.getAge());  // Default age indicating no medical record found
            assertTrue(johnDoeResponse.getMedications().isEmpty());  // No medications
            assertTrue(johnDoeResponse.getAllergies().isEmpty());  // No allergies
        }
    }
    @Test
    void testInvalidStationNumber() {
        // Mock DataService to return no addresses for an invalid station number
        when(dataService.getAddressesByStationNumber(99)).thenReturn(Collections.emptySet());

        // Call the service method
        Map<String, List<FloodResponse>> households = floodService.getFloodService(Arrays.asList(99));

        // Assert that the result is empty
        assertTrue(households.isEmpty());
    }



}
