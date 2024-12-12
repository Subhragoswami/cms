package com.continuum.cms.service;

import com.continuum.cms.auth.security.JwtService;
import com.continuum.cms.dao.ChargingSessionDao;
import com.continuum.cms.dao.VendorBankDao;
import com.continuum.cms.dao.VendorDetailsDao;
import com.continuum.cms.entity.ChargingSession;
import com.continuum.cms.entity.VendorBankDetails;
import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.exceptions.ValidationException;
import com.continuum.cms.mapper.ChargingSessionMapper;
import com.continuum.cms.model.external.requests.ChargingSessionFilter;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.model.response.ChargingSessionResponse;
import com.continuum.cms.service.external.VendorClientService;
import com.continuum.cms.util.ErrorConstants;
import com.continuum.cms.util.MiscUtil;
import com.continuum.cms.util.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;

import static com.continuum.cms.util.AppConstants.RESPONSE_SUCCESS;
import static com.continuum.cms.util.ErrorConstants.NOT_FOUND_ERROR_CODE;
import static com.continuum.cms.util.ErrorConstants.NOT_FOUND_ERROR_MESSAGE;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChargingSessionService {
    private final VendorClientService vendorClientService;
    private final VendorDetailsService vendorDetailsService;
    private final MiscUtil miscUtil;
    private final ChargingSessionDao chargingSessionDao;
    private final ChargingSessionMapper sessionMapper;
    private final VendorBankDao vendorBankDao;
    private final VendorDetailsDao vendorDetailsDao;

    private final JwtService jwtService;


    public Object getChargingSessionDetails(Pageable pageable, String search, ChargingSessionFilter chargingSessionFilter, String userName, String token) {
        String role = jwtService.extractRoles(token);
        log.info("Calling charging session by user role : {}", role);
        if(role.equalsIgnoreCase(Role.ROLE_SUPER_ADMIN.getName())){
            return getChargingSessionDetailsFromCMS(chargingSessionFilter, search, pageable);
        }else if(role.equalsIgnoreCase(Role.ROLE_VENDOR_ADMIN.getName())){
            String vendorEndpoint =  miscUtil.validateUserNameAndGetVendorEndPoint(userName);
            return vendorClientService.getVendorChargingSessionDetails(vendorEndpoint, pageable, search, chargingSessionFilter);
        }
        throw new CMSException(ErrorConstants.NOT_FOUND_ERROR_CODE, MessageFormat.format(ErrorConstants.NOT_FOUND_ERROR_MESSAGE, "User Role"));
    }

    public Object pullChargingSessionsData(String startDate, String endDate, String userName, Optional<UUID> vendorId) {
        String vendorEndpoint;
        if (vendorId.isPresent()) {
            VendorBankDetails vendorBankDetails = vendorDetailsDao.getVendorBankDetailsByVendorId(vendorId.get()).orElseThrow(() -> new ValidationException(NOT_FOUND_ERROR_CODE,
                    MessageFormat.format(NOT_FOUND_ERROR_MESSAGE, "Vendor Details")));
            vendorEndpoint = Optional.ofNullable(vendorDetailsService.getVendorCINToEndpointMap(vendorBankDetails.getCin())).orElseThrow(() ->
                    new ValidationException(NOT_FOUND_ERROR_CODE,
                            MessageFormat.format(NOT_FOUND_ERROR_MESSAGE, "Vendor to Endpoint Mapping")));
            return vendorClientService.pullChargingSessionsData(vendorEndpoint, startDate, endDate);
        } else {
            vendorEndpoint = miscUtil.validateUserNameAndGetVendorEndPoint(userName);
        }
        return vendorClientService.pullChargingSessionsData(vendorEndpoint, startDate, endDate);
    }

    public Object getAllTransactionsData(String userName, String token, Optional<UUID> vendorId, Pageable pageable) {
        String role = jwtService.extractRoles(token);
        String vendorEndpoint;
        log.info("Calling charging session by user role : {}", role);
        if (role.equalsIgnoreCase(Role.ROLE_SUPER_ADMIN.getName())) {
            vendorEndpoint = miscUtil.validateVendorIdAndGetVendorEndPoint(vendorId.get());
        } else if (role.equalsIgnoreCase(Role.ROLE_VENDOR_ADMIN.getName())) {
            vendorEndpoint = miscUtil.validateUserNameAndGetVendorEndPoint(userName);
        } else {
            throw new CMSException(ErrorConstants.NOT_FOUND_ERROR_CODE, MessageFormat.format(ErrorConstants.NOT_FOUND_ERROR_MESSAGE, "User Role"));
        }
        return vendorClientService.getAllTransactionsData(vendorEndpoint, pageable);
    }

    public Object getTransactionById(String userName, String transactionId) {
        String vendorEndpoint =  miscUtil.validateUserNameAndGetVendorEndPoint(userName);
        return vendorClientService.getTransactionById(vendorEndpoint, transactionId);
    }

    public ResponseDto<ChargingSessionResponse> getChargingSessionDetailsFromCMS(ChargingSessionFilter chargingSessionFilter, String search, Pageable pageable) {
        log.info("Fetching charging session details for super admin");
        List<VendorBankDetails> vendorBankDetailsList;
        List<String> vendorCodes = new ArrayList<>();
        if(!CollectionUtils.isEmpty(chargingSessionFilter.getVendorIds())){
            log.info("Fetching data from vendor details for specific vendor by vendorId : {}", chargingSessionFilter.getVendorIds());
            vendorBankDetailsList =vendorBankDao.getByVendorIdIn(chargingSessionFilter.getVendorIds());
            log.info("Successfully fetched vendor bank details.");
            vendorCodes = vendorBankDetailsList.stream()
                    .map(VendorBankDetails::getCin)
                    .toList();
        }
        log.info("Fetching charging session with criteria : {}, {}", chargingSessionFilter, search);
        Page<ChargingSession> chargingSessionsList = chargingSessionDao.getAllChargingSession(chargingSessionFilter, search, pageable, vendorCodes);
        log.info("Successfully fetched charging session details.");
        return ResponseDto.<ChargingSessionResponse>builder()
                .data(sessionMapper.mapToChargingSessionList(chargingSessionsList.getContent()))
                .status(RESPONSE_SUCCESS)
                .count(chargingSessionsList.stream().count())
                .total(chargingSessionsList.getTotalElements())
                .build();
    }
}
