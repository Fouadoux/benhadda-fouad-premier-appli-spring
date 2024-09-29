package com.safetyname.alerts.service;

import com.safetyname.alerts.controller.FloodController;
import com.safetyname.alerts.dto.FloodResponse;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for retrieving information about households covered by specific fire stations.
 * <p>
 * This service interacts with the data layer to fetch persons, addresses, and medical records
 * based on fire station numbers and compiles a list of {@link FloodResponse} objects containing
 * details about the individuals and their medical information.
 * </p>
 */
@Service
public class FloodService implements IFloodService {

    private static final Logger logger = LogManager.getLogger(FloodController.class);

    private IDataService dataService;

    /**
     * Constructor for FloodService that initializes the data service.
     *
     * @param dataService The data service used to access information about persons and their medical records.
     */
    public FloodService(IDataService dataService) {
        this.dataService = dataService;
    }

    /**
     * Retrieves a map of households covered by the specified fire stations.
     * <p>
     * The keys of the map represent the addresses, and the values are lists of {@link FloodResponse} objects
     * representing the individuals living at those addresses, along with their medical information.
     * </p>
     *
     * @param stationNumbers A list of fire station numbers to check for covered households.
     * @return A map where the keys are addresses and the values are lists of FloodResponse objects. Returns an empty map if no data is found.
     */
    public Map<String, List<FloodResponse>> getFloodService(List<Integer> stationNumbers) {

        logger.info("Request received for fire stations: {}", stationNumbers);


        // 1. Retrieve the addresses served by the specified fire stations
        Set<String> addresses = stationNumbers.stream()
                .flatMap(stationNumber -> dataService.getAddressesByStationNumber(stationNumber).stream())
                .collect(Collectors.toSet());

        if (addresses.isEmpty()) {
            logger.warn("No addresses found for fire stations: {}", stationNumbers);
            return Collections.emptyMap();
        }

        // 2. Retrieve the individuals living at these addresses
        List<Person> persons = addresses.stream()
                .flatMap(address -> dataService.getPersonsByAddress(address).stream())
                .collect(Collectors.toList());

        if (persons.isEmpty()) {
            logger.warn("No people found at addresses: {}", addresses);
            return Collections.emptyMap();
        }

        // 3. Retrieve the medical records for these individuals
        List<MedicalRecord> medicalRecords = dataService.getMedicalRecordsByPersons(persons);

        // Create a Map for the medical records using "FirstName LastName" as the key
        Map<String, MedicalRecord> medicalRecordMap = new HashMap<>();
        for (MedicalRecord record : medicalRecords) {
            String key = (record.getFirstName() + " " + record.getLastName()).trim().toLowerCase();
            medicalRecordMap.put(key, record);
        }

        // Create a Map to group households by address
        Map<String, List<FloodResponse>> householdsByAddress = new HashMap<>();

        // Iterate through all individuals
        for (Person person : persons) {
            String address = person.getAddress();
            String key = (person.getFirstName() + " " + person.getLastName()).trim().toLowerCase();

            // Retrieve the corresponding medical record
            MedicalRecord medicalRecord = medicalRecordMap.get(key);

            // Calculate age and retrieve medications and allergies
            int age = -1;
            List<String> medications = Collections.emptyList();
            List<String> allergies = Collections.emptyList();

            if (medicalRecord != null) {
                age = CalculateAgeService.calculateAge(medicalRecord.getBirthdate());
                medications = medicalRecord.getMedications();
                allergies = medicalRecord.getAllergies();
            }

            // Create the FloodResponse for the person
            FloodResponse response = new FloodResponse(person.getFirstName(), person.getLastName(), person.getPhone(), age, medications, allergies);

            // Add the FloodResponse to the corresponding list in the Map
            householdsByAddress.computeIfAbsent(address, k -> new ArrayList<>()).add(response);
        }

        return householdsByAddress;
    }
}
