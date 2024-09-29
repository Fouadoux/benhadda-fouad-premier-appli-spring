package com.safetyname.alerts.service;

import com.safetyname.alerts.dto.PersonInfoLastNameResponse;

import java.util.List;

public interface IPersonInfoLastNameService {
    List<PersonInfoLastNameResponse> getPersonInfoLastNameService(String lastName);

}
