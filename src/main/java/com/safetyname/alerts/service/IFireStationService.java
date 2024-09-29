package com.safetyname.alerts.service;

import com.safetyname.alerts.dto.FirestationResponse;

public interface IFireStationService {

    FirestationResponse getFireStationService(int stationNumber);
}
