package com.safetyname.alerts.controller;

import com.safetyname.alerts.dto.ChildResponse;

import com.safetyname.alerts.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/childAlert")
public class ChildAlertController {

    private static final Logger logger = LogManager.getLogger(ChildAlertController.class);

    private IChildAlertService childAlertService;


    public ChildAlertController(IChildAlertService childAlertService) {
        this.childAlertService = childAlertService;
    }


    @GetMapping
    public ResponseEntity<List<ChildResponse>> getChildAlert(@RequestParam("address") String address) {
        List<ChildResponse> children = childAlertService.getChildrenByAddress(address);

        if (children.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(children, HttpStatus.OK);
    }

}
