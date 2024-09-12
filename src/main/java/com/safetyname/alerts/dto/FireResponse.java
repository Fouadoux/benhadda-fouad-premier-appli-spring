package com.safetyname.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FireResponse {
   private List<FireInfo> fireInfos;
   private int station;

  /*  public FireResponse(List<FireInfo> fireinfos, int stationNumber) {
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
    }*/
}
