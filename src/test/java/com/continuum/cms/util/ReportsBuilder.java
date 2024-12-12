package com.continuum.cms.util;

import com.continuum.cms.entity.Report;

import java.util.UUID;

public class ReportsBuilder {
    public static Report buildReports(UUID id, UUID userId, String type, String filter, String status){
        return Report.builder()
                .id(id)
                .userId(userId)
                .type(type)
                .filter(filter)
                .status(status)
                .build();
    }
}
