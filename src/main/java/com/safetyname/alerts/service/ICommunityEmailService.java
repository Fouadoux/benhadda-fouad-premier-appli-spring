package com.safetyname.alerts.service;

import java.util.List;

public interface ICommunityEmailService {
    List<String> getEmailByCity (String city);
}
