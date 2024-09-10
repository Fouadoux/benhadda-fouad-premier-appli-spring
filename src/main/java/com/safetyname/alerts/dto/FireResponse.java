package com.safetyname.alerts.dto;

import java.util.List;

public class FireResponse {
   private List<FireInfo> fireinfos;
   private int stationNumber;

    public FireResponse(List<FireInfo> fireinfos, int stationNumber) {
        this.fireinfos = fireinfos;
        this.stationNumber = stationNumber;
    }

    public int getStationNumber() {
        return stationNumber;
    }

    public void setStationNumber(int stationNumber) {
        this.stationNumber = stationNumber;
    }

    public List<FireInfo> getFireinfos() {
        return fireinfos;
    }

    public void setFireinfos(List<FireInfo> fireinfos) {
        this.fireinfos = fireinfos;
    }
}
