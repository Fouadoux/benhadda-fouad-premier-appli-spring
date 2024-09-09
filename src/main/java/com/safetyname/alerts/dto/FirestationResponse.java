package com.safetyname.alerts.dto;

import com.safetyname.alerts.entity.Person;

import java.util.List;

public class FirestationReponse {
    private List<Person> persons;
    private long adultCount;
    private long childCount;

    public FirestationReponse(List<Person> persons, long adultCount, long childCount) {
        this.persons = persons;
        this.adultCount = adultCount;
        this.childCount = childCount;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public long getAdultCount() {
        return adultCount;
    }

    public void setAdultCount(long adultCount) {
        this.adultCount = adultCount;
    }

    public long getChildCount() {
        return childCount;
    }

    public void setChildCount(long childCount) {
        this.childCount = childCount;
    }
}
