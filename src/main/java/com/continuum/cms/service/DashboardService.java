package com.continuum.cms.service;

import com.continuum.cms.dao.DashboardDao;
import com.continuum.cms.model.response.DashboardResponse;
import com.continuum.cms.model.response.ResponseDto;

import com.continuum.cms.util.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardDao dashboardDao;
    private final CarbonCreditsService carbonCreditsService;

    public ResponseDto<DashboardResponse> getDashboardRecords() {
        log.info("Starting to get the dashboard data");
        DashboardResponse dashboardResponse = buildEmptyDashboardResponse();

        int vendorCount = Integer.valueOf((int) dashboardDao.getVendorCount());
        Integer adminCount = Integer.valueOf((int) dashboardDao.getAdminCount());
        Integer totalUsedEnergy = dashboardDao.getTotalUsedEnergy();
        Integer chargingSessionCount = Integer.valueOf((int) dashboardDao.getChargingSessionCount());
        Integer chargerDetailsCount = Integer.valueOf((int) dashboardDao.getChargerDetailsCount());
        double totalUtilization = dashboardDao.getCountTotalUtilization();
        dashboardResponse.setTotalUtilization(totalUtilization);
        dashboardResponse.setTotalEnergy(totalUsedEnergy);
        dashboardResponse.setTotalSessions(chargingSessionCount);
        dashboardResponse.setTotalDevice(chargerDetailsCount);
        dashboardResponse.setNumberOfAdmins(adminCount);
        dashboardResponse.setNumberOfVendor(vendorCount);
        dashboardResponse.setTotalCarbonCredit(carbonCreditsService.getTotalCarbonCreditsOfAllVendors().getTotalCarbonCreditsAvailable());

        log.info("Received all dashboard data from DB");

        return ResponseDto.<DashboardResponse>builder()
                .status(AppConstants.RESPONSE_SUCCESS)
                .data(List.of(dashboardResponse))
                .build();
    }

    private DashboardResponse buildEmptyDashboardResponse() {
        return DashboardResponse.builder()
                .chargerUtilization(DashboardResponse.ChargerUtilization.builder().build())
                .build();
    }

}
