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
public class ConnectorDetailsResponse {
    private UUID id;
    private String name;
    private String standardName;
    private String formatName;
    private String powerType;
    private String cmsId;
    private int maxVoltage;
    private int maxAmperage;
    private int maxElectricPower;
    private String termsConditionUrl;
    private String connectorImage;
    private String evseId;
    private String connectorId;
}

