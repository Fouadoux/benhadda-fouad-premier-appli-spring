package com.safetyname.alerts.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "Bienvenue sur mon application Spring Boot!";
    }
    @GetMapping("/index")
    public String index() {
        return "Ceci est la page index!";
    }
}