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


/**
 * REST controller for managing medical records.
 * <p>
 * This controller allows adding, updating, and deleting medical records.
 * Medical records contain information about a person's birthdate, medications, and allergies.
 */
@RestController
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

    private static final Logger logger = LogManager.getLogger(MedicalRecordController.class);

    private final DataService dataService;

    /**
     * Constructor for MedicalRecordController that initializes the data service.
     *
     * @param dataService The data service used to access medical records.
     * @throws IOException If an error occurs while reading the data file.
     */
    public MedicalRecordController(DataService dataService) throws IOException {
        this.dataService = dataService;
    }

    /**
     * Endpoint to add a new medical record.
     * <p>
     * If the first name or last name is missing, a 400 HTTP status is returned.
     * If the medical record already exists, a 409 HTTP status is returned.
     *
     * @param newMedicalRecord The new medical record to add.
     * @return ResponseEntity containing a success message or an error.
     */
    @PostMapping
    public ResponseEntity<String> addMedicalRecord(@RequestBody MedicalRecord newMedicalRecord) {
        logger.info("Received request to add medical record");
        if (newMedicalRecord.getFirstName().trim().isEmpty() || newMedicalRecord.getLastName().isEmpty()
        || newMedicalRecord.getBirthdate().trim().isEmpty()) {
            logger.warn("Bad request in addMedicalRecord - Missing firstName or lastName.");
            return new ResponseEntity<>("Bad request in addMedicalRecord", HttpStatus.BAD_REQUEST);
        }
        try {
            List<MedicalRecord> medicalRecords = dataService.getMedicalRecords();
            for (MedicalRecord medicalRecord : medicalRecords) {
                if (medicalRecord.getFirstName().equals(newMedicalRecord.getFirstName())&&medicalRecord.getLastName().equals(newMedicalRecord.getLastName())
                        && medicalRecord.getBirthdate().equals(newMedicalRecord.getBirthdate())) {
                    logger.warn("Conflict in addMedicalRecord - Existing medical record: {}", medicalRecord);
                    return new ResponseEntity<>("Conflict in addMedicalRecord", HttpStatus.CONFLICT);
                }
            }
            medicalRecords.add(newMedicalRecord);
            dataService.saveData();
            logger.info("Medical record added successfully");
            return new ResponseEntity<>("Medical record added successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to add medical record", e);
            return new ResponseEntity<>("Failed to add medical record: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to update an existing medical record.
     * <p>
     * If required fields are missing, a 400 HTTP status is returned.
     * If the medical record is not found, a 404 HTTP status is returned.
     *
     * @param updateMedicalRecord The medical record to update.
     * @return ResponseEntity containing a success message or an error.
     */
    @PutMapping
    public ResponseEntity<String> updateMedicalRecord(@RequestBody MedicalRecord updateMedicalRecord) {
        logger.info("Received request to update medical record");
        if (updateMedicalRecord.getFirstName().trim().isEmpty() ||
                updateMedicalRecord.getLastName().trim().isEmpty() || updateMedicalRecord.getBirthdate().trim().isEmpty()) {
            logger.warn("Bad request for updating medical record - Missing required fields: {}", updateMedicalRecord);
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
        try {
            List<MedicalRecord> medicalRecords = dataService.getMedicalRecords();
            for (MedicalRecord medicalRecord : medicalRecords) {
                if (medicalRecord.equals(updateMedicalRecord)) {
                    logger.error("Conflict in update medical record - Medical record already exists: {} {}", updateMedicalRecord.getFirstName(), updateMedicalRecord.getLastName());
                    return new ResponseEntity<>("Conflict in update medical record", HttpStatus.CONFLICT);
                }
                if (medicalRecord.getFirstName().equals(updateMedicalRecord.getFirstName()) &&
                        medicalRecord.getLastName().equals(updateMedicalRecord.getLastName())) {
                    medicalRecord.setBirthdate(updateMedicalRecord.getBirthdate());
                    medicalRecord.setMedications(updateMedicalRecord.getMedications());
                    medicalRecord.setAllergies(updateMedicalRecord.getAllergies());
                    dataService.saveData();
                    logger.info("Medical record updated successfully");
                    return new ResponseEntity<>("Medical record updated successfully", HttpStatus.OK);
                }
            }
            logger.warn("Medical record not found in updateMedicalRecord: {} {}", updateMedicalRecord.getFirstName(), updateMedicalRecord.getLastName());
            return new ResponseEntity<>("Medical record not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Failed to update medical record", e);
            return new ResponseEntity<>("Failed to update medical record: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to delete a medical record.
     * <p>
     * If the medical record is not found, a 404 HTTP status is returned.
     *
     * @param firstName First name of the person whose medical record is to be deleted.
     * @param lastName  Last name of the person whose medical record is to be deleted.
     * @return ResponseEntity containing a success message or an error.
     */
    @DeleteMapping
    public ResponseEntity<String> deleteMedicalRecord(@RequestParam String firstName, @RequestParam String lastName) {
        logger.info("Received request to delete medical record");
        if (firstName.trim().isEmpty() || lastName.trim().isEmpty() ) {
            logger.warn("Bad request for delete medical record");
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
        try {
            List<MedicalRecord> medicalRecords = dataService.getMedicalRecords();
            boolean removed = medicalRecords.removeIf(record ->
                    record.getFirstName().equals(firstName) && record.getLastName().equals(lastName)
            );

            if (removed) {
                dataService.saveData();
                logger.info("Medical record deleted successfully");
                return new ResponseEntity<>("Medical record deleted successfully", HttpStatus.OK);
            } else {
                logger.warn("Medical record not found in deleteMedicalRecord: {} {}", firstName, lastName);
                return new ResponseEntity<>("Medical record not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Failed to delete medical record", e);
            return new ResponseEntity<>("Failed to delete medical record: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
