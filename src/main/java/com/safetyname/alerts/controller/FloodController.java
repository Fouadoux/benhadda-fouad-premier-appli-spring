package com.safetyname.alerts.controller;

import com.safetyname.alerts.dto.FloodResponse;
import com.safetyname.alerts.entity.MedicalRecord;
import com.safetyname.alerts.entity.Person;
import com.safetyname.alerts.service.CalculateAgeService;
import com.safetyname.alerts.service.DataService;
import com.safetyname.alerts.service.IDataService;
import com.safetyname.alerts.service.IFloodService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/flood")
public class FloodController {

    private static final Logger logger = LogManager.getLogger(FloodController.class);
    private final IFloodService floodService;


    public FloodController(IFloodService floodService) {
        this.floodService = floodService;
    }


    @GetMapping("/stations")
    public ResponseEntity<Map<String, List<FloodResponse>>> getHouseholdsByStation(@RequestParam("stations") List<Integer> stationNumbers) {

        Map<String, List<FloodResponse>> householdsByAddress = floodService.getFloodService(stationNumbers);

        if (householdsByAddress.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(householdsByAddress, HttpStatus.OK);
    }
}
