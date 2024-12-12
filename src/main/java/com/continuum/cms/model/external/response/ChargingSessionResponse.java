package com.continuum.cms.model.external.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ChargingSessionResponse {
    private int connectorId;
    private String startAt;
    private String stopAt;
    private String cdrToken;
    private String name;
    private String locationName;
    private GeoLocation geoLocation;
    private String stationType;
    private int stationId;
    private String city;
    private String address;
    private String vehicleName;
    private String vehicleNumber;
    private String usedEnergy;
    private String currency;
    private String authorizationReference;
    private Double carbonCreditGenerated;
    private UserDetails userDetails;
}