package com.safetyname.alerts.dto;

import java.util.List;

public class ChildResponse {
    private String firstName;
    private String LastName;
    private int age;
    private List<String> family;

    public ChildResponse(String firstName, String lastName, int age, List<String> family) {
        this.firstName = firstName;
        this.LastName = lastName;
        this.age = age;
        this.family = family;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getFamily() {
        return family;
    }

    public void setFamily(List<String> family) {
        this.family = family;
    }
}
