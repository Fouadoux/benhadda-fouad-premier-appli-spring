package com.safetyname.alerts.service;

import com.safetyname.alerts.entity.FireStation;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static com.safetyname.alerts.utility.Constante.*;
import static org.junit.jupiter.api.Assertions.*;

class DataServiceTest {

    private DataService dataService;
    boolean result;

    @BeforeEach
    void setUp() throws IOException {
        dataService= new DataService();
        result  = dataService.readJsonFile(FILEPATH);
    }

    @Test
    public void testReadJsonDataFile()  {

        assertTrue(result,"La méthode readJsonFile devrait retourner true pour indiquer une lecture réussie du fichier JSON.");
    }


    @Test
    void getPersons()throws IOException {

        List<Person> persons =dataService.getPersons();

        assertNotNull(persons, "La liste des personnes ne devrait pas être nulle après la lecture du fichier JSON");
        assertEquals(23, persons.size(), "La taille de la liste des personnes devrait être de 23");

    }

    @Test
    void getFireStations() {
        List<FireStation> fireStations =dataService.getFireStations();

        assertNotNull(fireStations, "La liste des fireStations ne devrait pas être nulle après la lecture du fichier JSON");
        assertEquals(13, fireStations.size(), "La taille de la liste des fireStations devrait être de 13");
    }

    @Test
    void getMedicalRecords() {
        List<MedicalRecord> medicalRecords =dataService.getMedicalRecords();

        assertNotNull(medicalRecords, "La liste des medicalRecords ne devrait pas être nulle après la lecture du fichier JSON");
        assertEquals(23, medicalRecords.size(), "La taille de la liste des fireStations devrait être de 13");

    }

    @Test
    void saveData() throws IOException {

        // Vider le fichier avant le test
        FileWriter fileWriter = new FileWriter(FILEPATHTEST);
        fileWriter.write("");  // Écrire une chaîne vide pour vider le fichier
        fileWriter.close();

        // Appel de la méthode saveData
        boolean write = dataService.saveData(FILEPATHTEST);
        assertTrue(write, "La méthode saveData devrait retourner true si l'écriture réussit.");

        // Vérifier que le fichier n'est pas vide
        File file = new File(FILEPATHTEST);
        assertTrue(file.length() > 0, "Le fichier JSON ne devrait pas être vide après l'écriture des données.");
    }


}
