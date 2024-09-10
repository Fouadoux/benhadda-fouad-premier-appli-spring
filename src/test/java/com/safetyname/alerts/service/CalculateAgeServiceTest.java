package com.safetyname.alerts.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculateAgeServiceTest {

    @Test
    public void testCalculateAge_Sucessfully(){
        String testAge ="09/04/1989";
        int age=CalculateAgeService.calculateAge(testAge);
        assertEquals(35,age);
    }

    @Test
    public void testCalculateAgeWrongFormat(){
        String testAge ="15/15/1989";
        int age=CalculateAgeService.calculateAge(testAge);
        assertEquals(-1,age);
    }

}