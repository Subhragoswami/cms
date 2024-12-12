package com.continuum.cms.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportResponse {
    private UUID id;
    private String type;
    private String status;
    private String startDate;
    private String endDate;
    private UUID fileId;
    private String dateCreated;
}
