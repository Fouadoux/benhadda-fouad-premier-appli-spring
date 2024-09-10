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
        dataService = new DataService();
        result = dataService.readJsonFile("src/test/resources/dataRead.json");
    }

    @Test
    public void testReadJsonDataFile() {

        assertTrue(result, "La méthode readJsonFile devrait retourner true pour indiquer une lecture réussie du fichier JSON.");
    }

    @Test
    public void testReadJsonDataFile_fail() throws IOException {
        result = dataService.readJsonFile("src/test/resources/dataReadFail.json");
        assertFalse(result, "La méthode readJsonFile devrait retourner false pour indiquer que la lecture à échoué du fichier JSON.");


    }


    @Test
    void getPersons() throws IOException {

        List<Person> persons = dataService.getPersons();

        assertNotNull(persons, "La liste des personnes ne devrait pas être nulle après la lecture du fichier JSON");
        assertEquals(23, persons.size(), "La taille de la liste des personnes devrait être de 23");

    }

    @Test
    void getFireStations() {
        List<FireStation> fireStations = dataService.getFireStations();

        assertNotNull(fireStations, "La liste des fireStations ne devrait pas être nulle après la lecture du fichier JSON");
        assertEquals(13, fireStations.size(), "La taille de la liste des fireStations devrait être de 13");
    }

    @Test
    void getMedicalRecords() {
        List<MedicalRecord> medicalRecords = dataService.getMedicalRecords();

        assertNotNull(medicalRecords, "La liste des medicalRecords ne devrait pas être nulle après la lecture du fichier JSON");
        assertEquals(23, medicalRecords.size(), "La taille de la liste des fireStations devrait être de 13");

    }

    @Test
    void testsaveData_successfully() throws IOException {

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

    @Test
    void testSaveData_fail() throws IOException {
        boolean write = dataService.saveData("src/test/resources/dataWriteFail.json");
        assertFalse(write, "La méthode saveData retourne false.");
    }

    @Test
    public void testGetPersonByAdress_successfully() {
        List<Person> testPerson = dataService.getPersonByAddress("1509 Culver St");
        assertNotNull(testPerson);
        assertEquals(5, testPerson.size());
    }

    @Test
    public void testGetPersonByAdress_fail() {
        List<Person> testPerson = dataService.getPersonByAddress("");
        assertEquals(0, testPerson.size());
    }

    @Test
    public void getMedicalrecordByPerson_successfully() {
        List<Person> testPerson = dataService.getPersonByAddress("1509 Culver St");
        List<MedicalRecord> testMedical = dataService.getMedicalrecordByPerson(testPerson);
        assertNotNull(testMedical);
        assertEquals(5, testMedical.size());
    }

    @Test
    public void getMedicalrecordByPerson_fail() {
        List<Person> testPerson = dataService.getPersonByAddress("");
        List<MedicalRecord> testMedical = dataService.getMedicalrecordByPerson(testPerson);
        assertNotNull(testMedical);
        assertEquals(0, testMedical.size());
    }

    @Test
    public void testGetPersonsByStationNumber_Successfully() {
        List<Person> persons = dataService.getPersonsByStationNumber(3);
        assertNotNull(persons);
        assertEquals(11, persons.size());
    }

    @Test
    public void testGetPersonsByStationNumber_fail() {
        List<Person> persons = dataService.getPersonsByStationNumber(9);
        assertNotNull(persons);
        assertEquals(0, persons.size());
    }

}