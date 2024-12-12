package com.continuum.cms.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class CarbonCreditsVendorsResponse {
    private UUID vendorId;
    private String vendorName;
    private String vendorCode;
    private BigDecimal totalCarbonCreditGenerated;
    private double totalUtilization;
}
