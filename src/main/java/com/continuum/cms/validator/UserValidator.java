package com.continuum.cms.validator;

import com.continuum.cms.dao.VendorUserDao;
import com.continuum.cms.entity.Vendor;
import com.continuum.cms.entity.VendorPreference;
import com.continuum.cms.exceptions.ValidationException;
import com.continuum.cms.model.request.UserDetailsRequest;
import com.continuum.cms.model.request.UserPostRequest;
import com.continuum.cms.util.ValidationUtil;
import com.continuum.cms.util.enums.Role;
import com.continuum.cms.util.enums.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.continuum.cms.util.ErrorConstants.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserValidator {

    private final ValidationUtil validationUtil;
    private final VendorUserDao vendorUserDao;


    public void validateCreateUserRequest(UserPostRequest userPostRequest, Role roles) {
        validationUtil.validateNotEmpty(userPostRequest.getFirstName(), "First Name");
        validationUtil.validateNotEmpty(userPostRequest.getLastName(), "Last Name");

        validationUtil.validateNotEmpty(userPostRequest.getEmail(), "Email");
        validationUtil.validateEmail(userPostRequest.getEmail());

        validationUtil.validateNotEmpty(userPostRequest.getPhoneNumber(), "Phone number");
        validationUtil.validatePhoneNumber(userPostRequest.getPhoneNumber());

        if (Role.ROLE_VENDOR_ADMIN.equals(roles)) {
            validationUtil.validateNotEmpty(userPostRequest.getCin(), "CIN");
        }
    }

    public void validateAdminCreationRequest(Vendor vendor, Optional<VendorPreference> vendorPreference) {
        if (vendorUserDao.getUsersByVendorId(vendor.getId()).size() >= vendorPreference.get().getTotalMaxAdmin())
            throw new ValidationException(MAX_LIMIT_REACHED_VENDOR_USER_CREATION_ERROR_CODE, MAX_LIMIT_REACHED_VENDOR_USER_CREATION_MESSAGE);

        if (Status.INACTIVE.equals(vendor.getStatus().toUpperCase()))
            throw new ValidationException(VENDOR_INACTIVE_ERROR_CODE, VENDOR_INACTIVE_ERROR_MESSAGE);

        if (Status.BLOCKED.equals(vendor.getStatus().toUpperCase()) || Status.BLOCKED.equals(vendor.getStatus().toUpperCase()))
            throw new ValidationException(VENDOR_BLOCKED_ERROR_CODE, VENDOR_BLOCKED_ERROR_MESSAGE);
    }

    public void validateUserDetailsRequest(UserDetailsRequest userPostRequest, Role roles) {
        validationUtil.validateNotEmpty(userPostRequest.getFirstName(), "First Name");
        validationUtil.validateNotEmpty(userPostRequest.getLastName(), "Last Name");

        validationUtil.validateNotEmpty(userPostRequest.getEmail(), "Email");
        validationUtil.validateEmail(userPostRequest.getEmail());

        validationUtil.validateNotEmpty(userPostRequest.getPhoneNumber(), "Phone number");
        validationUtil.validatePhoneNumber(userPostRequest.getPhoneNumber());

    }
}
