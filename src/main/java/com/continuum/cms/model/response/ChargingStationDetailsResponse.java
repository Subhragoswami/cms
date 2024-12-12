package com.continuum.cms.model.response;

import com.continuum.cms.model.external.response.ChargerDetailsResponse;
import com.continuum.cms.model.external.response.ChargingStationResponse;
import com.continuum.cms.model.external.response.ConnectorDetailsResponse;
import com.continuum.cms.model.external.response.EvseDetailsResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ChargingStationDetailsResponse extends ChargingStationResponse {
    private BigDecimal carbonCreditGenerated;
    private List<ChargerDetailsResponse> chargerDetailsResponse;
    private List<EvseDetailsResponse> evseDetailsResponse;
    private List<ConnectorDetailsResponse> connectorDetailsResponse;

}
