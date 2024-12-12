package com.continuum.cms.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportFilter {
    private List<String> categories;
    private List<UUID> vendors;
    private Date startDate;
    private Date endDate;
}
