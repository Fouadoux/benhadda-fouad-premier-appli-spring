package com.safetyname.alerts.service;
import com.safetyname.alerts.dto.ChildResponse;
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

class ChildAlertServiceTest {

    @Mock
    private IDataService dataService;

    @InjectMocks
    private ChildAlertService childAlertService;

    private List<Person> persons;
    private List<MedicalRecord> medicalRecords;
    private List<MedicalRecord> noMedicalRecords;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock persons living at the address
        persons = Arrays.asList(
                new Person("John", "Doe", "123 Main St", "City", "john@example.com", 71100, "123-456-7890"),
                new Person("Jane", "Doe", "123 Main St", "City", "jane@example.com", 71100, "987-654-3210")
        );

        // Mock medical records for the persons
        medicalRecords = Arrays.asList(
                new MedicalRecord("John", "Doe", "01/01/2010", Arrays.asList("med1"), Arrays.asList("allergy1")),  // Child
                new MedicalRecord("Jane", "Doe", "01/01/1985", Arrays.asList("med2"), Arrays.asList("allergy2"))   // Adult
        );

        noMedicalRecords = Collections.emptyList();
    }

    @Test
    void testGetChildrenByAddressWithChildrenFound() {
        // Mock the behavior of dataService for the address "123 Main St"
        when(dataService.getPersonsByAddress("123 Main St")).thenReturn(persons);
        when(dataService.getMedicalRecordsByPersons(persons)).thenReturn(medicalRecords);

        // Call the service method
        List<ChildResponse> children = childAlertService.getChildrenByAddress("123 Main St");

        // Verify the result
        assertFalse(children.isEmpty());
        assertEquals(1, children.size());  // Only one child in the household
        assertEquals("John", children.get(0).getFirstName());
        assertEquals(14, children.get(0).getAge());  // Verify age based on birthdate (2010)
        assertEquals(1, children.get(0).getFamily().size());  // Verify family members
        assertEquals("Jane Doe", children.get(0).getFamily().get(0));  // Jane is an adult in the household
    }

    @Test
    void testGetChildrenByAddressWithNoChildren() {
        // Mock the behavior of dataService for the address "123 Main St"
        when(dataService.getPersonsByAddress("123 Main St")).thenReturn(persons);
        when(dataService.getMedicalRecordsByPersons(persons)).thenReturn(noMedicalRecords);

        // Call the service method
        List<ChildResponse> children = childAlertService.getChildrenByAddress("123 Main St");

        // Verify the result
        assertTrue(children.isEmpty());  // No children should be found
    }

    @Test
    void testGetChildrenByAddressWithNoPersonsFound() {
        // Mock the behavior of dataService for the address "123 Main St" with no persons
        when(dataService.getPersonsByAddress("123 Main St")).thenReturn(Collections.emptyList());

        // Call the service method
        List<ChildResponse> children = childAlertService.getChildrenByAddress("123 Main St");

        // Verify the result
        assertTrue(children.isEmpty());  // No persons, so no children should be found
    }
}
