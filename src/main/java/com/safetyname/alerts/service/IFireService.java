package com.safetyname.alerts.service;

import com.safetyname.alerts.dto.FireResponse;

public interface IFireService {
    FireResponse getFireService(String address);
}
