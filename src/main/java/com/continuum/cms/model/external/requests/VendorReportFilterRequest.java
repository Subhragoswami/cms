package com.continuum.cms.model.external.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendorReportFilterRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
