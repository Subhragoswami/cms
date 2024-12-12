package com.continuum.cms.util;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarbonCreditCSVContent {
    private BigDecimal totalCarbonCredit;
    private List<String> csvContentPerVendorData;
    private List<String> csvContentPerSessionData;
}
