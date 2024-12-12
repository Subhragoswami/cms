package com.continuum.cms.model.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class CarbonCreditDashboardViewResponse {

    private UUID vendorId;
    private String vendorName;
    private String vendorCode;
    private BigDecimal totalCarbonCredit;
    private Double totalUsedEnergy;
}
