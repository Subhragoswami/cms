package com.continuum.cms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "carbon_credit_dashboard_view")
public class CarbonCreditDashboard {

    @Id
    @Column(name = "vendor_id")
    private UUID vendorId;

    @Column(name = "vendor_name")
    private String vendorName;

    @Column(name = "vendor_code")
    private String vendorCode;

    @Column(name = "total_carbon_credit")
    private BigDecimal totalCarbonCredit;

    @Column(name = "total_used_energy")
    private Double totalUsedEnergy;


}
