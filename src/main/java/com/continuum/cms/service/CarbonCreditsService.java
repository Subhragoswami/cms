package com.continuum.cms.service;

import com.continuum.cms.auth.security.JwtService;
import com.continuum.cms.dao.CarbonCreditDao;
import com.continuum.cms.dao.VendorDetailsDao;
import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.entity.*;
import com.continuum.cms.exceptions.ValidationException;
import com.continuum.cms.mapper.CarbonCreditDashboardMapper;
import com.continuum.cms.model.external.requests.CarbonCreditsFilter;
import com.continuum.cms.model.external.response.CarbonCreditsResponse;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.model.response.CarbonCreditDashboardViewResponse;
import com.continuum.cms.service.external.VendorClientService;
import com.continuum.cms.util.ErrorConstants;
import com.continuum.cms.util.MiscUtil;
import com.continuum.cms.util.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;

import static com.continuum.cms.util.AppConstants.RESPONSE_SUCCESS;
import static com.continuum.cms.util.ErrorConstants.NOT_FOUND_ERROR_CODE;
import static com.continuum.cms.util.ErrorConstants.NOT_FOUND_ERROR_MESSAGE;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarbonCreditsService {
    private final VendorDetailsDao vendorDetailsDao;
    private final CarbonCreditDao carbonCreditDao;

    private final JwtService jwtService;
    private final VendorClientService vendorClientService;

    private final MiscUtil miscUtil;

    private final CarbonCreditDashboardMapper carbonCreditDashboardMapper;
    public ResponseDto<CarbonCreditDashboardViewResponse> getVendorCarbonCreditsList(CarbonCreditsFilter carbonCreditsFilter, String search, Pageable pageable) {
        Page<CarbonCreditDashboard> vendorCarbonCreditViews = carbonCreditDao.getAllVendorCarbonCredits(carbonCreditsFilter.getVendorIds(), search, pageable);
        return ResponseDto.<CarbonCreditDashboardViewResponse>builder()
                .status(RESPONSE_SUCCESS)
                .data(carbonCreditDashboardMapper.mapCarbonCreditListToResponse(vendorCarbonCreditViews, CarbonCreditDashboardViewResponse.class))
                .count(vendorCarbonCreditViews.stream().count())
                .total(vendorCarbonCreditViews.getTotalElements())
                .build();
    }


    public ResponseDto<CarbonCreditsResponse> getTotalCarbonCreditsOfVendors() {
        return ResponseDto.<CarbonCreditsResponse>builder()
                .data(List.of(getTotalCarbonCreditsOfAllVendors()))
                .status(RESPONSE_SUCCESS).build();
    }

    public CarbonCreditsResponse getTotalCarbonCreditsOfAllVendors() {
        BigDecimal totalCarbonCredit = BigDecimal.ZERO;

        List<CarbonCreditDetail> allCarbonCreditsData = carbonCreditDao.getAllCarbonCreditsData();
        for (CarbonCreditDetail data : allCarbonCreditsData) {
            totalCarbonCredit = totalCarbonCredit.add(data.getCreditScorePoints());
        }
        log.info("Got the totalCarbonCredit");
        return CarbonCreditsResponse.builder()
                .totalCarbonCreditsAvailable(totalCarbonCredit)
                .totalCertificateSold(0)
                .totalCertificateAvailable(0).build();
    }

    public ResponseDto<CarbonCreditsResponse> getCarbonCreditsDetailsByVendorId(String userName, Optional<UUID> vendorId, String token) {
        String role = jwtService.extractRoles(token);
        if (role.equalsIgnoreCase(Role.ROLE_SUPER_ADMIN.getName())) {
            VendorBankDetails vendorBankDetails = vendorDetailsDao.getVendorBankDetailsByVendorId(vendorId.get()).orElseThrow(() -> new ValidationException(NOT_FOUND_ERROR_CODE,
                    MessageFormat.format(NOT_FOUND_ERROR_MESSAGE, "Vendor Id")));
            return getCarbonCreditDetailsOfVendor(vendorBankDetails.getCin());
        } else if (role.equalsIgnoreCase(Role.ROLE_VENDOR_ADMIN.getName())) {
            return getVendorCarbonDetailsForVendorAdmin(userName);
        }
        throw new CMSException(ErrorConstants.NOT_FOUND_ERROR_CODE, MessageFormat.format(ErrorConstants.NOT_FOUND_ERROR_MESSAGE, "User Role"));
    }

    private ResponseDto<CarbonCreditsResponse> getVendorCarbonDetailsForVendorAdmin(String userName) {
        String vendorEndpoint = miscUtil.validateUserNameAndGetVendorEndPoint(userName);
        ResponseDto<BigDecimal> responseDto = vendorClientService.getVendorCarbonCredits(vendorEndpoint);
        BigDecimal totalCarbonCredit = BigDecimal.ZERO;
        if (responseDto.getStatus() == RESPONSE_SUCCESS) {
            totalCarbonCredit = responseDto.getData().get(0);
        }
        CarbonCreditsResponse carbonCreditsResponse = CarbonCreditsResponse.builder()
                .totalCarbonCreditsAvailable(totalCarbonCredit)
                .totalCertificateSold(0)
                .totalCertificateAvailable(0)
                .build();
        return ResponseDto.<CarbonCreditsResponse>builder()
                .data(List.of(carbonCreditsResponse))
                .status(RESPONSE_SUCCESS)
                .build();
    }

    private ResponseDto<CarbonCreditsResponse> getCarbonCreditDetailsOfVendor(String vendorCode) {
        log.info("Calling getCarbonCreditDetailsOfVendor for getting CarbonCreditDetails by vendorCode {}",vendorCode);
        BigDecimal totalCarbonCredit = BigDecimal.ZERO;

        List<CarbonCreditDetail> allCarbonCreditsData = carbonCreditDao.getAllCarbonCreditsData();
        for (CarbonCreditDetail data : allCarbonCreditsData) {
            if(data.getCompositeId().getVendorCode().equals(vendorCode)){
                totalCarbonCredit = totalCarbonCredit.add(data.getCreditScorePoints());
            }
        }
        CarbonCreditsResponse carbonCreditsResponse = CarbonCreditsResponse.builder()
                .totalCarbonCreditsAvailable(totalCarbonCredit)
                .totalCertificateSold(0)
                .totalCertificateAvailable(0)
                .build();

        return ResponseDto.<CarbonCreditsResponse>builder()
                .data(List.of(carbonCreditsResponse))
                .status(RESPONSE_SUCCESS)
                .build();
    }
}
