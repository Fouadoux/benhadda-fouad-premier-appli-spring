package com.safetyname.alerts.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CalculateAgeService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final Logger logger = LogManager.getLogger(CalculateAgeService.class);

    public static int calculateAge(String birthdateStr) {
        try {
            LocalDate birthdate = LocalDate.parse(birthdateStr, formatter);
            return Period.between(birthdate, LocalDate.now()).getYears();
        } catch (DateTimeParseException e) {
            logger.error("Format de date invalide : {}", birthdateStr);
            return -1; // Retourne -1 si la date est invalide
        }
    }

}
