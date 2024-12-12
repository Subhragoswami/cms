package com.continuum.cms.model.external.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class CarbonCreditsResponse {
    private int totalCertificateAvailable;
    private int totalCertificateSold;
    private BigDecimal totalCarbonCreditsAvailable;
}
