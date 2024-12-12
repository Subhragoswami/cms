package com.continuum.cms.util;

import com.continuum.cms.dao.VendorDetailsDao;
import com.continuum.cms.entity.VendorBankDetails;
import com.continuum.cms.exceptions.ValidationException;
import com.continuum.cms.service.VendorDetailsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;

import static com.continuum.cms.util.ErrorConstants.NOT_FOUND_ERROR_CODE;
import static com.continuum.cms.util.ErrorConstants.NOT_FOUND_ERROR_MESSAGE;

@Component
@Slf4j
@AllArgsConstructor
public class MiscUtil {
    @Lazy
    @Autowired
    private final VendorDetailsDao vendorDetailsDao;
    private final VendorDetailsService vendorDetailsService;

    public String validateUserNameAndGetVendorEndPoint(String userName) {
        log.info("Validating UserName, Vendor Details and Vendor Endpoint");
        UUID vendorId = vendorDetailsDao.findVendorIdByUserName(userName).orElseThrow(() -> new ValidationException(NOT_FOUND_ERROR_CODE,
                MessageFormat.format(NOT_FOUND_ERROR_MESSAGE, "UserName")));
        VendorBankDetails vendorBankDetails = vendorDetailsDao.getVendorBankDetailsByVendorId(vendorId).orElseThrow(() -> new ValidationException(NOT_FOUND_ERROR_CODE,
                MessageFormat.format(NOT_FOUND_ERROR_MESSAGE, "Vendor Details")));
        return Optional.ofNullable(vendorDetailsService.getVendorCINToEndpointMap(vendorBankDetails.getCin())).orElseThrow(() ->
                new ValidationException(NOT_FOUND_ERROR_CODE,
                        MessageFormat.format(NOT_FOUND_ERROR_MESSAGE, "Vendor to Endpoint Mapping")));
    }

    public String validateVendorIdAndGetVendorEndPoint( UUID vendorId) {
        log.info("Validating VendorId Vendor Endpoint");
        VendorBankDetails vendorBankDetails = vendorDetailsDao.getVendorBankDetailsByVendorId(vendorId).orElseThrow(() -> new ValidationException(NOT_FOUND_ERROR_CODE,
                MessageFormat.format(NOT_FOUND_ERROR_MESSAGE, "Vendor Details")));
        return Optional.ofNullable(vendorDetailsService.getVendorCINToEndpointMap(vendorBankDetails.getCin())).orElseThrow(() ->
                new ValidationException(NOT_FOUND_ERROR_CODE,
                        MessageFormat.format(NOT_FOUND_ERROR_MESSAGE, "Vendor to Endpoint Mapping")));
    }
}
