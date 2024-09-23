package com.safetyname.alerts.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetyname.alerts.entity.FireStation;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;

import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;



/**
 * Service class for managing data related to persons, fire stations, and medical records.
 * <p>
 * This class provides methods to read data from a JSON file, save data to a file,
 * and retrieve information based on various criteria.
 * <p>
 * The methods {@link #saveData()} and {@link #readJsonFile()} are overloaded to accept
 * a file path as a parameter for test purposes, allowing different file paths to be used
 * during testing to avoid impacting the actual application data.
 */
@Service
public class DataService {

    private static final Logger logger = LogManager.getLogger(DataService.class);

    private List<Person> persons;
    private List<FireStation> firestations;
    private List<MedicalRecord> medicalRecords;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${data.filepathRead}")
    private String filePathRead;

    @Value("${data.filepathWrite}")
    private String filePathWrite;


    /**
     * Default constructor for DataService.
     */
    public DataService() {
    }

    /**
     * Reads the default JSON file specified in the application properties.
     *
     * @return true if the data was successfully read, false otherwise.
     */
    @PostConstruct
    public boolean readJsonFile(){
        return readJsonFile(filePathRead);
    }

    /**
     * Overloaded method to read data from a specified JSON file.
     * <p>
     * This method is primarily used for testing purposes, allowing a custom file path to be provided.
     *
     * @param filePathRead The path of the JSON file to read from.
     * @return true if the data was successfully read and processed, false otherwise.
     */
    public boolean readJsonFile(String filePathRead) {

        logger.info("Reading JSON file from: {}", filePathRead);
        try {
            Map<String, Object> data = mapper.readValue(new File(filePathRead),
                    new TypeReference<Map<String, Object>>() {
                    });

            this.persons = mapper.convertValue(data.get("persons"), new TypeReference<List<Person>>() {
            });
            this.firestations = mapper.convertValue(data.get("firestations"), new TypeReference<List<FireStation>>() {
            });
            this.medicalRecords = mapper.convertValue(data.get("medicalrecords"), new TypeReference<List<MedicalRecord>>() {
            });
            logger.info("Data successfully read and processed.");
            return true;

        } catch (IOException e) {
            logger.error("Failed to read data from file: {}", filePathRead, e);
            return false;
        }
    }

    /**
     * Saves the current data to the default JSON file specified in the application properties.
     *
     * @return true if the data was successfully saved, false otherwise.
     */
    public boolean saveData(){
        return saveData(filePathWrite);
    }

    /**
     * Overloaded method to save the current data to a specified JSON file.
     * <p>
     * This method is primarily used for testing purposes, allowing a custom file path to be provided.
     *
     * @param filePath The path to the file where data should be saved.
     * @return true if the data was successfully saved, false otherwise.
     */
    public boolean saveData(String filePath) {
        logger.info("Saving data to: {}", filePath);
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("persons", persons);
            data.put("firestations", firestations);
            data.put("medicalrecords", medicalRecords);
            mapper.writeValue(new File(filePath), data);
            logger.info("Data successfully saved.");
            return true;
        } catch (IOException e) {
            logger.error("Failed to save data to file: {}", filePath, e);
            return false;
        }
    }

    /**
     * Retrieves the list of all persons.
     *
     * @return A list of {@link Person} objects.
     */
    public List<Person> getPersons() {
        logger.info("Retrieving list of all persons.");
        return persons;
    }

    /**
     * Retrieves the list of all fire stations.
     *
     * @return A list of {@link FireStation} objects.
     */
    public List<FireStation> getFireStations() {
        logger.info("Retrieving list of all fire stations.");
        return firestations;
    }

    /**
     * Retrieves the list of all medical records.
     *
     * @return A list of {@link MedicalRecord} objects.
     */
    public List<MedicalRecord> getMedicalRecords() {
        logger.info("Retrieving list of all medical records.");
        return medicalRecords;
    }

    /**
     * Retrieves a list of persons covered by a specific fire station number.
     *
     * @param stationNumber The fire station number.
     * @return A list of {@link Person} objects covered by the specified fire station.
     */
    public List<Person> getPersonsByStationNumber(int stationNumber) {
        logger.info("Retrieving persons for fire station number: {}", stationNumber);
        List<FireStation> fireStations = getFireStations();
        List<String> addresses = fireStations.stream()
                .filter(fs -> fs.getStation() == stationNumber)
                .map(FireStation::getAddress)
                .collect(Collectors.toList());
        return getPersons().stream()
                .filter(person -> addresses.contains(person.getAddress()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves medical records for a list of persons.
     *
     * @param persons The list of {@link Person} objects.
     * @return A list of {@link MedicalRecord} objects corresponding to the given persons.
     */
    public List<MedicalRecord> getMedicalRecordsByPersons(List<Person> persons) {
        logger.info("Retrieving medical records for given persons.");
        if (persons == null || persons.isEmpty()) {
            logger.warn("The list of persons is null or empty. No medical records will be searched.");
            return Collections.emptyList();
        }
        List<MedicalRecord> medicalRecords = getMedicalRecords();
        return medicalRecords.stream().filter(record -> persons.stream()
                .anyMatch(person -> person.getFirstName().equals(record.getFirstName()) &&
                        person.getLastName().equals(record.getLastName()))).collect(Collectors.toList());
    }

    /**
     * Retrieves a list of persons living at a specific address.
     *
     * @param address The address to search for.
     * @return A list of {@link Person} objects living at the specified address.
     */
    public List<Person> getPersonsByAddress(String address) {
        logger.info("Retrieving persons living at address: {}", address);
        if (address == null || address.isEmpty()) {
            logger.warn("The address is null or empty. No persons will be searched.");
            return Collections.emptyList();
        }
        return getPersons().stream()
                .filter(person -> person.getAddress().equals(address))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the fire station number serving a specific address.
     *
     * @param address The address to search for.
     * @return The station number serving the address, or -1 if not found.
     */
    public int getStationByAddress(String address) {
        logger.info("Retrieving station number for address: {}", address);
        if (address == null || address.isEmpty()) {
            logger.warn("The address is null or empty. No station will be searched.");
            return -1;
        }
        List<FireStation> fireStations = getFireStations();
        return fireStations.stream()
                .filter(fireStation -> fireStation.getAddress().equals(address))
                .map(FireStation::getStation)
                .findFirst()
                .orElse(-1);
    }

    /**
     * Retrieves a list of persons with a specific last name.
     *
     * @param lastName The last name to search for.
     * @return A list of {@link Person} objects with the specified last name.
     */
    public List<Person> getPersonsByLastName(String lastName) {
        logger.info("Retrieving persons with last name: {}", lastName);
        if (lastName == null || lastName.isEmpty()) {
            logger.warn("The last name is null or empty. No persons will be searched.");
            return Collections.emptyList();
        }
        return getPersons().stream()
                .filter(person -> person.getLastName().equals(lastName))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a set of addresses served by a specific fire station number.
     *
     * @param stationNumber The fire station number.
     * @return A set of addresses served by the specified fire station.
     */
    public Set<String> getAddressesByStationNumber(int stationNumber) {
        logger.info("Retrieving addresses for fire station number: {}", stationNumber);
        if (stationNumber == 0 ) {
            logger.warn("No station exists with this number.");
            return Collections.emptySet();
        }
        List<FireStation> fireStations = getFireStations();
        long test= fireStations.stream()
                .filter(fireStation -> fireStation.getStation()==stationNumber)
                .count();
                if (test==0)
        {
            logger.warn("No addresses found for station number: {}", stationNumber);
            return Collections.emptySet();
        }
        return fireStations.stream()
                .filter(fireStation -> fireStation.getStation() == stationNumber)
                .map(FireStation::getAddress)
                .collect(Collectors.toSet());
    }
}
