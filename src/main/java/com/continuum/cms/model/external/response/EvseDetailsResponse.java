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
public class EvseDetailsResponse {
    private UUID id;
    private String physicalReference;
    private String chargerId;
    private String maxOutputPower;
    private String status;
    private int connectorId;
    private String connectorStatus;
    private String availability;
    private String evseId;
}
