package com.safetyname.alerts.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Service class for calculating age based on birthdate.
 * <p>
 * This class provides a method to calculate the age of a person given their birthdate in a specific format.
 */
public class CalculateAgeService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final Logger logger = LogManager.getLogger(CalculateAgeService.class);

    /**
     * Calculates the age based on the given birthdate string.
     * <p>
     * If the birthdate string is invalid or cannot be parsed, the method returns -1.
     *
     * @param birthdateStr The birthdate string in "MM/dd/yyyy" format.
     * @return The age in years, or -1 if the birthdate is invalid.
     */
    public static int calculateAge(String birthdateStr) {
        logger.info("Calculating age for birthdate: {}", birthdateStr);
        try {
            LocalDate birthdate = LocalDate.parse(birthdateStr, formatter);
            int age = Period.between(birthdate, LocalDate.now()).getYears();
            logger.info("Calculated age: {}", age);
            return age;
        } catch (DateTimeParseException e) {
            logger.error("Invalid date format: {}", birthdateStr, e);
            return -1; // Return -1 if the date is invalid
        }
    }
}
