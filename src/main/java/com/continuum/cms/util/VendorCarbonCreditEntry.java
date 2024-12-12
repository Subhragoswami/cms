package com.continuum.cms.util;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendorCarbonCreditEntry {
    private String vendorName;
    private UUID vendorId;
    private BigDecimal totalPoints;

}
