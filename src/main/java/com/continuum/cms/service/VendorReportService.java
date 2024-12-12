package com.continuum.cms.service;

import com.continuum.cms.dao.VendorDetailsDao;
import com.continuum.cms.entity.VendorUser;
import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.model.external.requests.VendorCarbonCreditFilterRequest;
import com.continuum.cms.model.external.requests.VendorReportFilterRequest;
import com.continuum.cms.model.external.response.ReportResponse;
import com.continuum.cms.model.external.response.VendorReportDetailsResponse;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.service.external.VendorClientService;
import com.continuum.cms.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class VendorReportService {
    private final MiscUtil miscUtil;
    private final VendorClientService vendorClientService;
    private final VendorDetailsDao vendorDetailsDao;
    public Object getCarbonCreditDetails(String userName, VendorCarbonCreditFilterRequest vendorCarbonCreditFilterRequest) throws Exception {
        log.info("Validating userName and getting vendor endpoint for user: {}", userName);
        String vendorEndpoint = miscUtil.validateUserNameAndGetVendorEndPoint(userName);
        VendorUser vendorUser = vendorDetailsDao.findVendorUserDetails(userName);
        Object vendorReport = vendorClientService.getVendorReport(vendorEndpoint, vendorCarbonCreditFilterRequest, vendorUser.getUser().getId() , vendorUser.getVendor().getId());
        log.info("Vendor report fetched successfully for user: {}, vendor: {}", vendorUser.getUser().getId(), vendorUser.getVendor().getId());
        return vendorReport;
    }

    public Object getAllReportsData(Pageable pageable, VendorReportFilterRequest vendorReportFilterRequest, String userName){
        log.info("Validating userName and getting vendor endpoint for user: {}", userName);
        String vendorEndpoint = miscUtil.validateUserNameAndGetVendorEndPoint(userName);
        Object vendorReport = vendorClientService.getAllVendorReportData(vendorEndpoint, pageable, vendorReportFilterRequest);
        log.info("Vendor report data fetched successfully for user: {}", userName);
        return vendorReport;
    }
}

