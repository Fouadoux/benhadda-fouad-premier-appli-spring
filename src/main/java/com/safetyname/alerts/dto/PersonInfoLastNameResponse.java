package com.safetyname.alerts.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter

public class PersonInfoLastNameResponse {

    private String lastName;
    private String address;
    private int age;
    private String email;
    private List<String > medications;
    private List<String > allergies;

    public PersonInfoLastNameResponse(String lastName , String address, int age, String email, List<String> allergies, List<String> medications) {

        this.allergies = allergies;
        this.medications = medications;
        this.email = email;
        this.age = age;
        this.address = address;
        this.lastName = lastName;
    }

}
