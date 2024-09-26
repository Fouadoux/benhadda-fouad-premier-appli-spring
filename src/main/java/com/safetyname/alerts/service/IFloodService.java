package com.safetyname.alerts.service;

import com.safetyname.alerts.dto.FloodResponse;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface IFloodService {
    Map<String, List<FloodResponse>> getFloodService( List<Integer> stationNumbers);
}
