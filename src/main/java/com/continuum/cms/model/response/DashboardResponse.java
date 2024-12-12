package com.continuum.cms.model.response;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class DashboardResponse implements Serializable {

    private Integer totalDevice;
    private Integer totalEnergy;
    private Double totalUtilization;
    private Integer totalSessions;
    private Integer activeCharging;
    private BigDecimal totalCarbonCredit;
    private Integer onlineDevice;
    private Integer offlineDevice;
    private Integer idleDevice;
    private Integer numberOfVendor;
    private Integer numberOfAdmins;
    private ChargerUtilization chargerUtilization;

    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    public static class ChargerUtilization implements Serializable {
        private Integer used;
        private Integer notUsed;
    }

}
