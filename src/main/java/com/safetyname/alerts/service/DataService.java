package com.safetyname.alerts.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetyname.alerts.entity.FireStation;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.safetyname.alerts.utility.Constante.*;



@Service
public class DataService {

    private List<Person> persons;
    private List<FireStation> firestations;
    private List<MedicalRecord> medicalRecords;
    private final ObjectMapper mapper = new ObjectMapper();
    //private final String filePath = "src/main/resources/data.json";

    public DataService() {
    }
    //faire un boolean pour le test unitaite. si true ok
    public boolean readJsonFile(String filePath) throws IOException{
        try {
            // lecture du fichier Json avec un map car on a plusieurs structure
            Map<String, Object> data = mapper.readValue(new File(FILEPATH),
                    new TypeReference<Map<String, Object>>() {
                    });

            // Convertir chaque partie du JSON dans la bonne structure
            this.persons = mapper.convertValue(data.get("persons"), new TypeReference<List<Person>>() {
            });
            this.firestations = mapper.convertValue(data.get("firestations"), new TypeReference<List<FireStation>>() {
            });
            this.medicalRecords = mapper.convertValue(data.get("medicalrecords"), new TypeReference<List<MedicalRecord>>() {
            });
            return true;

        } catch (IOException e) {
            e.printStackTrace();
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
            Map<String, Object> data = new HashMap<>();
            data.put("persons", persons);
            data.put("firestations",firestations);
            data.put("medicalrecords",medicalRecords);
            mapper.writeValue(new File(filePath), data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
