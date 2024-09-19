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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Test class for {@link DataService}.
 * <p>
 * This class contains unit tests for the DataService methods,
 * verifying data reading, writing, and retrieval operations.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class DataServiceTest {


    private static final Logger logger = LogManager.getLogger(DataServiceTest.class);

    @Autowired
    private DataService dataService;

    boolean result;

    /**
     * Sets up the test environment before each test.
     * Initializes the DataService and reads the test JSON file.
     */
    @BeforeEach
    void setUp() {
        dataService.readJsonFile();
    }

    /**
     * Tests the readJsonFile method with a valid file.
     * Expects the method to return true indicating a successful read.
     */
    @Test
    public void testReadJsonDataFile() {
        logger.info("Testing readJsonFile method with a valid JSON file.");
        result = dataService.readJsonFile();
        assertTrue(result, "The readJsonFile method should return true to indicate successful reading of the JSON file.");
    }

    /**
     * Tests the readJsonFile method with an invalid file.
     * Expects the method to return false indicating a failed read.
     */
    @Test
    public void testReadJsonDataFile_fail() {
        logger.info("Testing readJsonFile method with an invalid JSON file.");

        result = dataService.readJsonFile("src/test/resources/dataError.json");
        assertFalse(result, "The readJsonFile method should return false to indicate that reading the JSON file failed.");
    }

    /**
     * Tests the getPersons method.
     * Expects the list of persons to be non-null and have a size of 23 after reading the JSON file.
     */
    @Test
    void getPersons() {
        logger.info("Testing getPersons method.");

        List<Person> persons = dataService.getPersons();

        assertNotNull(persons, "The list of persons should not be null after reading the JSON file.");
        assertEquals(23, persons.size(), "The size of the list of persons should be 23.");
    }

    /**
     * Tests the getFireStations method.
     * Expects the list of fire stations to be non-null and have a size of 13 after reading the JSON file.
     */
    @Test
    void getFireStations() {
        logger.info("Testing getFireStations method.");

        List<FireStation> fireStations = dataService.getFireStations();

        assertNotNull(fireStations, "The list of fire stations should not be null after reading the JSON file.");
        assertEquals(13, fireStations.size(), "The size of the list of fire stations should be 13.");
    }

    /**
     * Tests the getMedicalRecords method.
     * Expects the list of medical records to be non-null and have a size of 23 after reading the JSON file.
     */
    @Test
    void getMedicalRecords() {
        logger.info("Testing getMedicalRecords method.");

        List<MedicalRecord> medicalRecords = dataService.getMedicalRecords();

        assertNotNull(medicalRecords, "The list of medical records should not be null after reading the JSON file.");
        assertEquals(23, medicalRecords.size(), "The size of the list of medical records should be 23.");
    }

    /**
     * Tests the saveData method successfully.
     * Expects the method to return true and the file to not be empty after writing.
     *
     * @throws IOException if an I/O error occurs during the test.
     */
    @Test
    void testSaveData_successfully() throws IOException {
        logger.info("Testing saveData method successfully.");

        // Empty the file before the test
        FileWriter fileWriter = new FileWriter("src/test/resources/dataRead.json");
        fileWriter.write("");  // Write an empty string to empty the file
        fileWriter.close();

        // Call the saveData method
        boolean write = dataService.saveData();
        assertTrue(write, "The saveData method should return true if writing succeeds.");

        // Verify that the file is not empty
        File file = new File("src/test/resources/dataRead.json");
        assertTrue(file.length() > 0, "The JSON file should not be empty after writing the data.");
    }

    /**
     * Tests the saveData method with a failure.
     * Expects the method to return false.
     */
    @Test
    void testSaveData_fail() {
        logger.info("Testing saveData method with a failure.");

        boolean write = dataService.saveData("src/test/resources/dataWriteFail.json");
        assertFalse(write, "The saveData method should return false.");
    }

    /**
     * Tests the getPersonsByAddress method successfully.
     * Expects to retrieve a list of persons living at the specified address.
     */
    @Test
    public void testGetPersonByAddress_successfully() {
        logger.info("Testing getPersonsByAddress method successfully.");

        List<Person> testPerson = dataService.getPersonsByAddress("1509 Culver St");
        assertNotNull(testPerson);
        assertEquals(5, testPerson.size());
    }

    /**
     * Tests the getPersonsByAddress method with a failure.
     * Expects to retrieve an empty list when the address is empty.
     */
    @Test
    public void testGetPersonByAddress_fail() {
        logger.info("Testing getPersonsByAddress method with an empty address.");

        List<Person> testPerson = dataService.getPersonsByAddress("");
        assertEquals(0, testPerson.size());
    }

    /**
     * Tests the getMedicalRecordsByPersons method successfully.
     * Expects to retrieve a list of medical records for the specified persons.
     */
    @Test
    public void getMedicalRecordsByPersons_successfully() {
        logger.info("Testing getMedicalRecordsByPersons method successfully.");

        List<Person> testPerson = dataService.getPersonsByAddress("1509 Culver St");
        List<MedicalRecord> testMedical = dataService.getMedicalRecordsByPersons(testPerson);
        assertNotNull(testMedical);
        assertEquals(5, testMedical.size());
    }

    /**
     * Tests the getMedicalRecordsByPersons method with a failure.
     * Expects to retrieve an empty list when the list of persons is empty.
     */
    @Test
    public void getMedicalRecordsByPersons_fail() {
        logger.info("Testing getMedicalRecordsByPersons method with an empty list of persons.");

        List<Person> testPerson = dataService.getPersonsByAddress("");
        List<MedicalRecord> testMedical = dataService.getMedicalRecordsByPersons(testPerson);
        assertNotNull(testMedical);
        assertEquals(0, testMedical.size());
    }

    /**
     * Tests the getPersonsByStationNumber method successfully.
     * Expects to retrieve a list of persons covered by the specified station number.
     */
    @Test
    public void testGetPersonsByStationNumber_Successfully() {
        logger.info("Testing getPersonsByStationNumber method successfully.");

        List<Person> persons = dataService.getPersonsByStationNumber(3);
        assertNotNull(persons);
        assertEquals(11, persons.size());
    }

    /**
     * Tests the getPersonsByStationNumber method with a failure.
     * Expects to retrieve an empty list when no persons are covered by the station number.
     */
    @Test
    public void testGetPersonsByStationNumber_fail() {
        logger.info("Testing getPersonsByStationNumber method with no persons found.");

        List<Person> persons = dataService.getPersonsByStationNumber(9);
        assertNotNull(persons);
        assertEquals(0, persons.size());
    }

    /**
     * Tests the getStationByAddress method successfully.
     * Expects to retrieve the station number associated with the specified address.
     */
    @Test
    public void testGetStationByAddress_successfully() {
        logger.info("Testing getStationByAddress method successfully.");

        int station = dataService.getStationByAddress("1509 Culver St");
        assertEquals(3, station);
    }

    /**
     * Tests the getStationByAddress method with a wrong address.
     * Expects to retrieve -1 indicating the address is not associated with any station.
     */
    @Test
    public void testGetStationByAddress_wrongAddress() {
        logger.info("Testing getStationByAddress method with a wrong address.");

        int station = dataService.getStationByAddress("159 Culver St");
        assertEquals(-1, station);
    }

    /**
     * Tests the getStationByAddress method with no content.
     * Expects to retrieve -1 indicating the address is empty or null.
     */
    @Test
    public void testGetStationByAddress_noContent() {
        logger.info("Testing getStationByAddress method with no content.");

        int station = dataService.getStationByAddress("");
        assertEquals(-1, station);
    }

    /**
     * Tests the getPersonsByLastName method successfully.
     * Expects to retrieve a list of persons with the specified last name.
     */
    @Test
    public void testGetPersonsByLastName_successfully() {
        logger.info("Testing getPersonsByLastName method successfully.");

        List<Person> persons = dataService.getPersonsByLastName("Boyd");
        assertNotNull(persons);
        assertEquals(6, persons.size());
    }

    /**
     * Tests the getPersonsByLastName method with a failure.
     * Expects to retrieve an empty list when no persons have the specified last name.
     */
    @Test
    public void testGetPersonsByLastName_fail() {
        logger.info("Testing getPersonsByLastName method with no persons found.");

        List<Person> persons = dataService.getPersonsByLastName("Bod");
        assertNotNull(persons);
        assertEquals(0, persons.size());
    }

    /**
     * Tests the getPersonsByLastName method with no content.
     * Expects to retrieve an empty list when the last name is empty.
     */
    @Test
    public void testGetPersonsByLastName_noContent() {
        logger.info("Testing getPersonsByLastName method with no content.");

        List<Person> persons = dataService.getPersonsByLastName("");
        assertNotNull(persons);
        assertEquals(0, persons.size());
    }

    /**
     * Tests the getAddressesByStationNumber method of the DataService class.
     * <p>
     * This class contains unit tests that verify the behavior of the method when
     * different station numbers are provided.
     */
    @Test
    void testGetAddressesByStationNumberSuccess(){
         Set<String> addressList = dataService.getAddressesByStationNumber(1);

         assertEquals(3,addressList.size());
    }

    /**
     * Tests the getAddressesByStationNumber method when the station number is zero.
     * <p>
     * Expects the method to return an empty set of addresses when 0 is passed as the station number.
     */
    @Test
    void testGetAddressesByStationNumberWhenStationNumberIsZero(){
        Set<String> addressList = dataService.getAddressesByStationNumber(0);

        assertEquals(0,addressList.size());
    }

    /**
     * Tests the getAddressesByStationNumber method when an invalid station number is provided.
     * <p>
     * Expects the method to return an empty set of addresses when an invalid station number
     * (like 3598678) is passed.
     */
    @Test
    void testGetAddressesByStationNumberWhenStationNumberIsInvalid(){
        Set<String> addressList = dataService.getAddressesByStationNumber(3598678);

        assertEquals(0,addressList.size());
    }


}
