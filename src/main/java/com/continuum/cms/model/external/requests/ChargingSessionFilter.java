package com.continuum.cms.model.external.requests;


import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChargingSessionFilter {
    private List<String> cities;
    private Date startDate;
    private Date endDate;

    private List<UUID> vendorIds;
    private List<Long> stationIds;
}