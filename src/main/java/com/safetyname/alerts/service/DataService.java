package com.safetyname.alerts.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetyname.alerts.entity.FireStation;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class DataService {

    private static final Logger logger = LogManager.getLogger(DataService.class);

    private List<Person> persons;
    private List<FireStation> firestations;
    private List<MedicalRecord> medicalRecords;
    private final ObjectMapper mapper = new ObjectMapper();
    @Value("${data.filepath}")
    private String filePath;

    public DataService() {
    }


    //@PostConstruct
    public boolean readJsonFile(String filePath) {
        try {
            // lecture du fichier Json avec un map car on a plusieurs structure
            logger.debug("reading JSON file from : {}", filePath);
            Map<String, Object> data = mapper.readValue(new File(filePath),
                    new TypeReference<Map<String, Object>>() {
                    });

            // Convertir chaque partie du JSON dans la bonne structure
            this.persons = mapper.convertValue(data.get("persons"), new TypeReference<List<Person>>() {
            });
            this.firestations = mapper.convertValue(data.get("firestations"), new TypeReference<List<FireStation>>() {
            });
            this.medicalRecords = mapper.convertValue(data.get("medicalrecords"), new TypeReference<List<MedicalRecord>>() {
            });
            logger.info("Data successfully read and processed.");
            return true;

        } catch (IOException e) {
            logger.error("failde to read data from file: {}", filePath);
            return false;
        }
    }

    public List<Person> getPersons() {
        return persons;
    }

    public List<FireStation> getFireStations() {
        return firestations;
    }

    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    public boolean saveData(String filePath) {
        try {
            logger.debug("Saving data to: {}", filePath);
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

    public List<Person> getPersonsByStationNumber(int stationNumber) {
        List<FireStation> fireStations = getFireStations();
        List<String> address = fireStations.stream()
                .filter(fs -> fs.getStation() == stationNumber)
                .map(FireStation::getAddress)
                .toList();
        return getPersons().stream()
                .filter(person -> address.contains(person.getAddress()))
                .collect(Collectors.toList());
    }

    public List<MedicalRecord> getMedicalrecordByPerson(List<Person> objects) {
        if (persons == null || persons.isEmpty()) {
            logger.warn("La liste des personnes est null ou vide. Aucun dossier médical ne sera recherché.");
            return Collections.emptyList();
        }
        List<MedicalRecord> medicalRecords = getMedicalRecords();
        return medicalRecords.stream().filter(record -> objects.stream()
                .anyMatch(person -> person.getFirstName().equals(record.getFirstName()) &&
                        person.getLastName().equals(record.getLastName()))).toList();

    }

    public List<Person> getPersonByAddress(String address) {
        if (address == null || address.isEmpty()) {
            logger.warn("L'adresse est null ou vide. Aucune personne ne sera recherché.");
            return Collections.emptyList();
        }
        return getPersons().stream()
                .filter(person -> person.getAddress().equals(address))
                .collect(Collectors.toList());
    }

    public int getSationByAddress(String address) {
        List<FireStation> fireStations = getFireStations();
        return fireStations.stream()
                .filter(fireStation -> fireStation.getAddress().equals(address))
                .map(FireStation::getStation)
                .findFirst()
                .orElse(-1);

    }





}
