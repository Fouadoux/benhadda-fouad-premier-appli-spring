package com.safetyname.alerts.alerts.controller;
/*
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home (Model model){
        model.addAttribute("message","Bonjour sur la page d'acceuil !");
        return "home";
    }
}*/
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