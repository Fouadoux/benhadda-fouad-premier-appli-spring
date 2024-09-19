package com.safetyname.alerts.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Test class for {@link CalculateAgeService}.
 * <p>
 * This class contains unit tests for the CalculateAgeService methods,
 * verifying correct age calculation and handling of incorrect date formats.
 */
class CalculateAgeServiceTest {

    private static final Logger logger = LogManager.getLogger(CalculateAgeServiceTest.class);

    /**
     * Tests the calculateAge method with a valid date.
     * Expects the correct age to be returned.
     */
    @Test
    public void testCalculateAgeSuccessfully() {
        logger.info("Testing calculateAge method successfully with a valid date.");

        String testDate = "09/04/1989";
        int age = CalculateAgeService.calculateAge(testDate);
        assertEquals(35, age);
    }

    /**
     * Tests the calculateAge method with an incorrectly formatted date.
     * Expects -1 to be returned indicating an error.
     */
    @Test
    public void testCalculateAgeWrongFormat() {
        logger.info("Testing calculateAge method with an incorrectly formatted date.");

        String testDate = "15/15/1989";
        int age = CalculateAgeService.calculateAge(testDate);
        assertEquals(-1, age);
    }
}
