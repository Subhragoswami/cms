package com.continuum.cms.model.external.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {
    private double totalCarbonCreditGenerated;
    private LocalDateTime startDate;
    private  LocalDateTime endDate;
    private List<VendorReportDetailsResponse> vendorReportResponses;
}
