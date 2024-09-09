package com.safetyname.alerts.dto;

import com.safetyname.alerts.entity.Person;

import java.util.List;

public class FirestationResponse {
    private List<PersonInfo> persons;
    private long adultCount;
    private long childCount;

    public FirestationResponse(List<PersonInfo> persons, long adultCount, long childCount) {
        this.persons = persons;
        this.adultCount = adultCount;
        this.childCount = childCount;
    }

    public List<PersonInfo> getPersons() {
        return persons;
    }

    public void setPersons(List<PersonInfo> persons) {
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
