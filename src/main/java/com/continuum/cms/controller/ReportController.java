package com.continuum.cms.controller;

import com.continuum.cms.model.external.requests.VendorCarbonCreditFilterRequest;
import com.continuum.cms.model.external.requests.VendorReportFilterRequest;
import com.continuum.cms.model.request.ReportFilter;
import com.continuum.cms.model.response.ReportResponse;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.service.ReportService;
import com.continuum.cms.service.VendorReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {
    private final ReportService reportService;
    private final VendorReportService vendorReportService;
    @PostMapping
    public CompletableFuture<ResponseDto<String>> getCSVFile(@RequestBody ReportFilter reportFilter) {
        log.info("Request for generate reports filter:{}", reportFilter);
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return reportService.generateReport(userName, reportFilter);
    }

    @PostMapping("/list")
    public ResponseDto<ReportResponse> getReportList(
            @PageableDefault(sort = {"dateCreated"}, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestBody(required = false) ReportFilter reportFilter) {
        log.info("Fetching report list with search: {} and filter: {}", search, reportFilter);
        return reportService.reportList(reportFilter, search, pageable);
    }

    @PostMapping("/vendor")
    public Object getCarbonCreditDetails(HttpServletResponse response,
                                                      @RequestBody(required = false) VendorCarbonCreditFilterRequest vendorCarbonCreditFilterRequest) throws Exception {
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        log.info("Received request to get vendor Report details");
        return vendorReportService.getCarbonCreditDetails(userName, vendorCarbonCreditFilterRequest);
    }

    @PostMapping("/vendor/list")
    public Object getAllReports(@PageableDefault(sort = {"dateCreated"}, direction = Sort.Direction.DESC) Pageable pageable,
                                @RequestBody(required = false) VendorReportFilterRequest vendorReportFilterRequest) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        log.info("Received request to get vendor Report details");
        return vendorReportService.getAllReportsData(pageable, vendorReportFilterRequest, userName);
    }

}

