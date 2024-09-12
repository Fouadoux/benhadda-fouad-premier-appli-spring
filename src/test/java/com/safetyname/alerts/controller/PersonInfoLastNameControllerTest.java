package com.safetyname.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.DataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import static org.mockito.Mockito.when;

@WebMvcTest(PersonInfoLastNameController.class)
class PersonInfoLastNameControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    DataService dataService;

    @Test
    void testGetPersonInfolastName_PersonAndMedicalRecordFound() throws Exception {

        List<Person> persons = Arrays.asList(
                new Person("John", "Doe", "123 Main St", "City1", "john@example.com", 71100, "123-456-7890"),
                new Person("Jane", "Doe", "456 Oak St", "City1", "jane@example.com", 71100, "987-654-3210")
        );

        List <MedicalRecord> medicalRecords=new ArrayList<>();
        List<String> medications1 = Arrays.asList("Aspirin", "Ibuprofen");
        List<String> allergies1 = Arrays.asList("Peanuts", "Pollen");
        List<String> medications2 = Arrays.asList("Paracetamol", "Antibiotics");
        List<String> allergies2 = Arrays.asList("Dust", "Cats");
        medicalRecords.add(new MedicalRecord("John","Doe","04/09/1989",allergies1,medications1));
        medicalRecords.add(new MedicalRecord("Jane","Doe","25/12/2006",allergies2,medications2));
        String lastName = "Doe";
        when(dataService.getPersonByLastName(lastName)).thenReturn(persons);
        when(dataService.getMedicalrecordByPerson(persons)).thenReturn(medicalRecords);

        mockMvc.perform(get("/personInfolastName/" + lastName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].address").value("123 Main St"))
                .andExpect(jsonPath("$[0].medications[0]").value("Aspirin"))
                .andExpect(jsonPath("$[0].allergies[0]").value("Peanuts"))
                .andExpect(jsonPath("$[1].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].address").value("456 Oak St"))
                .andExpect(jsonPath("$[1].medications[0]").value("Paracetamol"))
                .andExpect(jsonPath("$[1].allergies[0]").value("Dust"));

    }

    @Test
    void testGetPersonInfolastName_EmptyLastName() throws Exception {
        // Cas où le nom de famille est vide
        mockMvc.perform(get("/personInfolastName/ "))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testGetPersonInfolastName_PersonAndMedicalRecordNoMatch() throws Exception {
        String lastName = "Doe";
        List<String> medications2 = Arrays.asList("Paracetamol", "Antibiotics");
        List<String> allergies2 = Arrays.asList("Dust", "Cats");
        List<Person> personsA = Arrays.asList(new Person("John", "Doe", "123 Main St",
                "City1", "john@example.com", 71100, "123-456-7890"));
        List<MedicalRecord> medicalRecordsA = Arrays.asList(
                new MedicalRecord("Jane","smith","25/12/2006",allergies2,medications2));

        when(dataService.getPersonByLastName(lastName)).thenReturn(personsA);
        when(dataService.getMedicalrecordByPerson(personsA)).thenReturn(medicalRecordsA);

        mockMvc.perform(get("/personInfolastName/" + lastName))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPersonInfolastName_NoMedicalRecordsFound() throws Exception {
        String lastName = "Doe";
        List<Person> persons = Arrays.asList(new Person("John", "Doe", "123 Main St",
                "City1", "john@example.com", 71100, "123-456-7890"));
        when(dataService.getPersonByLastName(lastName)).thenReturn(persons);
        when(dataService.getMedicalrecordByPerson(persons)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/personInfolastName/" + lastName))
                .andExpect(status().isNotFound());
    }
    @Test
    void testGetPersonInfolastName_NoPersonFound() throws Exception {
        String lastName = "Doe";
        when(dataService.getPersonByLastName(lastName)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/personInfolastName/" + lastName))
                .andExpect(status().isNotFound());
    }

}