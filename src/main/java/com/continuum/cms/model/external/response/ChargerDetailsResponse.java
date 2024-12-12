package com.continuum.cms.model.external.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ChargerDetailsResponse {
    private UUID id;
    private String identity;
    private String chargerName;
    private String chargePointOem;
    private String chargePointDevice;
    private String chargePointConnectionProtocol;
    private String floorLevel;
    private String qrCode;
    private String chargerId;
    private String stationType;
    private long locationId;
    private String chargerStatus;

}
