package com.safetyname.alerts.alerts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetyname.alerts.alerts.enity.FireStation;
import com.safetyname.alerts.alerts.enity.MedicalRecord;
import com.safetyname.alerts.alerts.enity.Person;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@Service
public class DataService {

    private List<Person> persons;
    private List<FireStation> fireStations;
    private List<MedicalRecord> medicalRecords;

    public DataService(){
        ObjectMapper mapper = new ObjectMapper();
        try{
            Map<String,List<?>> data = mapper.readValue(new File("src/main/resources/data.json"),Map.class);
            this.persons=(List<Person>) data.get("persons");
            this.fireStations=(List<FireStation>) data.get("firestations");
            this.medicalRecords=(List<MedicalRecord>) data.get("medicalrecords");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public List<Person> getPersons() {
        return persons;
    }

    public List<FireStation> getFireStations() {
        return fireStations;
    }

    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }
}
