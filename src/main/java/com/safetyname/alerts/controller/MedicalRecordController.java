package com.safetyname.alerts.controller;

import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.service.DataService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.safetyname.alerts.utility.Constante.FILEPATH;

public class MedicalRecordController {

    private static final Logger logger= LogManager.getLogger(MedicalRecordController.class);

    private final DataService dataService;

    public MedicalRecordController(DataService dataService)throws IOException {
        this.dataService=dataService;
        this.dataService.readJsonFile(FILEPATH);
    }

    @PostMapping
    public ResponseEntity<String> addMedicalRecord(@RequestBody MedicalRecord newMedicalRecord){
        if(newMedicalRecord.getFirstName().isEmpty() || newMedicalRecord.getLastName().isEmpty()
                ||newMedicalRecord.getBirthdate().isEmpty()){
            logger.error("Bad request from add medical record");
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
        try{
            List<MedicalRecord> medicalRecords=dataService.getMedicalRecords();
            for(MedicalRecord medicalRecord : medicalRecords){
                if(medicalRecord.equals(newMedicalRecord)){
                    logger.error("Conflict in addPerson - Existing person: {}", medicalRecord);
                    return new ResponseEntity<>("Conflit", HttpStatus.CONFLICT);
                }
            }
            medicalRecords.add(newMedicalRecord);
            dataService.saveData(FILEPATH);
            logger.info("medical record added successfully");
            return new ResponseEntity<>("Medical record added sucessfully",HttpStatus.CREATED);
        }catch (Exception e) {
            logger.error("Failed to add medical record");
            return new ResponseEntity<>("Failed to add medical record: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
    // Mettre à jour un medical record
    @PutMapping
    public ResponseEntity<String> updateMedicalRecord(@RequestBody MedicalRecord updateMedicalRecord) {
        if (updateMedicalRecord.getFirstName().isEmpty()||
                updateMedicalRecord.getLastName().isEmpty()||updateMedicalRecord.getBirthdate().isEmpty())
        {
            logger.error("Bad request from updateMedicalRecord - Missing required fields: {}", updateMedicalRecord);
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
        try{
            List<MedicalRecord> medicalRecords=dataService.getMedicalRecords();
            for(MedicalRecord medicalRecord: medicalRecords){
                if(medicalRecord.getFirstName().equals(updateMedicalRecord.getFirstName()) &&
                        medicalRecord.getLastName().equals(updateMedicalRecord.getLastName())){
                    medicalRecord.setBirthdate(updateMedicalRecord.getBirthdate());
                    medicalRecord.setMedications(updateMedicalRecord.getMedications());
                    medicalRecord.setAllergies(updateMedicalRecord.getAllergies());
                    dataService.saveData(FILEPATH);
                    logger.info("Medical record updated successfully");
                    return new ResponseEntity<>("Medical record updated successfully", HttpStatus.OK);
                }
            }
            logger.error("Medical record not found - Existing medical record {} {}",updateMedicalRecord.getFirstName(),updateMedicalRecord.getLastName());
            return new ResponseEntity<>("Medical record not found",HttpStatus.NOT_FOUND);
        }catch (Exception e) {
            logger.error("Failed to update medical record", e);
            return new ResponseEntity<>("Failed to medical record: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Supprime une medical record
    @DeleteMapping
    public ResponseEntity<String> deletePerson(@RequestParam String firstName, @RequestParam String lastName) {
        try {
            List<MedicalRecord> medicalRecords = dataService.getMedicalRecords();
            boolean removed = medicalRecords.removeIf(person ->
                    person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)
            );

            if (removed) {
                dataService.saveData(FILEPATH);
                logger.info("Medical record deleted successfully");
                return new ResponseEntity<>("Medical record deleted successfully", HttpStatus.OK);
            } else {
                logger.error("Medical record not found {}, {} ",firstName,lastName);
                return new ResponseEntity<>("Medical record not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Failed to delete Medical record");
            return new ResponseEntity<>("Failed to delete Medical record : " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




















}
