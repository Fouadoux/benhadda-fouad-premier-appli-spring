package com.safetyname.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.DataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonController.class)
class PersonControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    DataService dataService;

    ObjectMapper objectMapper = new ObjectMapper();
    List<Person> persons;

    @BeforeEach
    void setUp() {
        persons=new ArrayList<>();
        persons.add(new Person("Fouad","Benhadda","19 rue pasteur",
                "Chalon-sur-saone","fouad@gmail.com",71100,"0673648562"));
        when(dataService.getPersons()).thenReturn(persons);
    }

    @Test
    void updatePersonTest() throws Exception {
        Person updatedPerson = new Person("Fouad","Benhadda","21 rue pasteur",
                "paris","fouad@gmail.com",75007,"0673648562");
        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPerson)))
                .andExpect(status().isOk())
                .andExpect(content().string("Person updated successfully"));
        verify(dataService).saveData(anyString());
    }

    @Test
    void failUpdatePersonTest() throws Exception{
        Person updatedPerson = new Person("Fouad","benhadda","21 rue pasteur",
                "paris","fouad@gmail.com",75007,"0673648562");
        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPerson)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Person not found"));
    }

    @Test
    public void testUpdatePerson_InvalidData() throws Exception {
        Person invalidPerson = new Person("", "", "19 rue pasteur", "paris",
                "fouad@gmail.com", 75001, "0673648562");

        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPerson)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdatePerson_NoContent() throws Exception{

        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addPerson() throws Exception {
        Person newPerson = new Person("Pierre", "Dupont", "14 rue tintin",
                "bruxelles", "54321", 55009, "0673648562");
        mockMvc.perform(post("/persons"));

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPerson)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Person added successfully"));

        verify(dataService).saveData(anyString());
    }

    @Test
    void testAddPerson_InvalidData()throws Exception{
        Person invalidPerson = new Person("", "", "", "", "", 0, "");
        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPerson)))
                .andExpect(status().isBadRequest());

    }
    @Test
    void testPersonDuplicate()throws Exception{
        Person addPerson = new Person("Fouad","Benhadda","19 rue pasteur",
                "Chalon-sur-saone","fouad@gmail.com",71100,"0673648562");
        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addPerson)))
                .andExpect(status().isConflict());

    }

    @Test
    public void testDeletePerson_Success() throws Exception {

        mockMvc.perform(delete("/person")
                        .param("firstName", "Fouad")
                        .param("lastName", "Benhadda"))
                .andExpect(status().isOk())
                .andExpect(content().string("Person deleted successfully"));

        verify(dataService).saveData(anyString());
    }
    @Test
    public void testDeletePerson_NotFound() throws Exception {

        mockMvc.perform(delete("/person")
                        .param("firstName", "Pierre")
                        .param("lastName", "Benhadda"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Person not found"));
    }

}