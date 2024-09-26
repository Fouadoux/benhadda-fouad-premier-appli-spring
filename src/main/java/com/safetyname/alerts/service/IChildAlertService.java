package com.safetyname.alerts.service;

import com.safetyname.alerts.dto.ChildResponse;

import java.util.List;

public interface IChildAlertService {
    List<ChildResponse> getChildrenByAddress(String address);
}
