package com.continuum.cms.controller;

import com.continuum.cms.model.response.DashboardResponse;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @Operation(summary = "Getting All Dashboard data")
    public ResponseDto<DashboardResponse> getDashboardData() {
        log.info("Received request to get all dashboard data.");
        return dashboardService.getDashboardRecords();
    }

}
