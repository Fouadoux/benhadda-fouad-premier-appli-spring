package com.safetyname.alerts.service;

import com.safetyname.alerts.entity.FireStation;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;

import java.util.List;
import java.util.Set;

public interface IDataService {
     boolean readJsonFile();
     boolean readJsonFile(String filePathRead);
     boolean saveData();
     boolean saveData(String filePath);
     List<Person> getPersons();
     List<FireStation> getFireStations();
     List<MedicalRecord> getMedicalRecords();
     List<Person> getPersonsByStationNumber(int stationNumber);
     List<MedicalRecord> getMedicalRecordsByPersons(List<Person> persons);
     List<Person> getPersonsByAddress(String address);
     int getStationByAddress(String address);
     List<Person> getPersonsByLastName(String lastName);
     Set<String> getAddressesByStationNumber(int stationNumber);
}
