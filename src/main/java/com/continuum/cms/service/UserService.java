package com.continuum.cms.service;

import com.continuum.cms.auth.security.JwtService;
import com.continuum.cms.config.CMSServiceConfig;
import com.continuum.cms.dao.UserDao;
import com.continuum.cms.dao.VendorDetailsDao;
import com.continuum.cms.dao.VendorPreferenceDao;
import com.continuum.cms.entity.*;
import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.exceptions.ValidationException;
import com.continuum.cms.model.request.UserDetailsUpdateRequest;
import com.continuum.cms.model.request.UserPostRequest;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.model.response.UserDetailsResponse;
import com.continuum.cms.util.DateUtil;
import com.continuum.cms.util.EncryptionUtil;
import com.continuum.cms.util.enums.Actions;
import com.continuum.cms.util.enums.Role;
import com.continuum.cms.util.enums.Status;
import com.continuum.cms.validator.UserValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.continuum.cms.util.AppConstants.RESPONSE_SUCCESS;
import static com.continuum.cms.util.ErrorConstants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserValidator userValidator;
    private final FileStorageService fileStorageService;

    private final CMSServiceConfig config;
    private final CMSServiceConfig cmsServiceConfig;

    private final EmailService emailService;
    private final JwtService jwtService;

    private final VendorDetailsDao vendorDetailsDao;
    private final VendorPreferenceDao vendorPreferenceDao;
    private final UserDao userDao;

    private final ObjectMapper mapper;

    public ResponseDto<String> saveUser(UserPostRequest userPostRequest, Role roles) {
        userValidator.validateCreateUserRequest(userPostRequest, roles);
        User user = buildUserObject(userPostRequest);
        if (Role.ROLE_VENDOR_ADMIN.equals(roles)) {
            log.info("Starting Creation of Admin, Request: {}", userPostRequest);
            Vendor vendor = vendorDetailsDao.getVendorByCin(userPostRequest.getCin()).stream().findFirst()
                    .orElseThrow(() -> new ValidationException(INVALID_ERROR_CODE, MessageFormat.format(INVALID_ERROR_CODE_MESSAGE, "vendor code"))).getVendor();
            Roles role = userDao.findRoleByName(Role.ROLE_VENDOR_ADMIN.getName()).orElseThrow(
                    () -> new CMSException(NOT_FOUND_ERROR_CODE, MessageFormat.format(NOT_FOUND_ERROR_MESSAGE,"Role")));
            UserRoles userRole = UserRoles.builder()
                    .role(role)
                    .user(user)
                    .build();
            Optional<VendorPreference> vendorPreference = vendorPreferenceDao.getVendorPreferenceByVendorId(vendor.getId());
            userValidator.validateAdminCreationRequest(vendor, vendorPreference);
            userDao.createVendorUser(user, vendor, userRole);
        }

        String token = jwtService.generateJWTToken(user, DateUtils.addHours(DateUtil.getDate(), cmsServiceConfig.getResetTokenExpiryTime()), Role.ROLE_VENDOR_ADMIN.getName());
        userDao.saveUserPasswordAudit(user, token, Status.PENDING, Actions.RESET_PASSWORD);
        emailService.newUserOnboardingEmail(user, token);
        return ResponseDto.<String>builder().status(RESPONSE_SUCCESS).data(List.of("Saved user Successfully")).build();
    }

    public ResponseDto<UserDetailsResponse> getUserDetails(String userName) {
        log.info("Fetching user details for userName: {}", userName);
        User user = userDao.getUserDetails(userName)
                .orElseThrow(() -> new CMSException(INVALID_ERROR_CODE, MessageFormat.format(INVALID_ERROR_CODE_MESSAGE, "userName")));
        log.info("Fetching vendor details for userId: {}", user.getId());
        Optional<VendorUser> vendorUser = userDao.getUserByUserId(user.getId());
        UserDetailsResponse userDetailsResponse = mapper.convertValue(user, UserDetailsResponse.class);
        vendorUser.ifPresentOrElse(
                vu -> userDetailsResponse.setVendorId(vu.getVendor().getId()),
                () -> userDetailsResponse.setVendorId(null)
        );
        log.info("Returning user details response for userName: {}", userName);
        return ResponseDto.<UserDetailsResponse>builder()
                .status(RESPONSE_SUCCESS)
                .data(List.of(userDetailsResponse))
                .build();
    }

    public ResponseDto<String> updateUserDetails(UUID userId, UserDetailsUpdateRequest userDetailsUpdateRequest) {
        log.info("Fetching user details for userId: {}", userId);
        User user = userDao.getUserDetailsById(userId)
                .orElseThrow(() -> new CMSException(INVALID_ERROR_CODE, MessageFormat.format(INVALID_ERROR_CODE_MESSAGE, "UserId")));
        log.info("Starting updation of user details");
        if(ObjectUtils.isNotEmpty(userDetailsUpdateRequest.getPhoneNumber()) && userDao.existsByPhoneNumber(userDetailsUpdateRequest.getPhoneNumber())){
            throw new CMSException(FIELD_ALREADY_EXIST_ERROR_CODE, MessageFormat.format(FIELD_ALREADY_EXIST_ERROR_CODE_MESSAGE, "phoneNumber"));
        };
        if(ObjectUtils.isNotEmpty(userDetailsUpdateRequest)) {
            mergeNonNullFields(user, userDetailsUpdateRequest);
        }
        log.info("User details updated successfully for userId: {}", userId);
        userDao.updateUser(user);
            return ResponseDto.<String>builder()
                    .status(RESPONSE_SUCCESS)
                    .data(List.of("updated successfully"))
                    .build();
    }

    public Optional<User> getUserByUserName(String userName) {
        return userDao.getByUserName(userName);
    }

    private User buildUserObject(UserPostRequest userPostRequest) {
        return User.builder()
                .userName(userPostRequest.getEmail())
                .firstName(userPostRequest.getFirstName())
                .lastName(userPostRequest.getLastName())
                .email(userPostRequest.getEmail())
                .phoneNumber(userPostRequest.getPhoneNumber())
                .status(Status.ACTIVE.name())
                .password(EncryptionUtil.encrypt(userPostRequest.getEmail(), config.getAppSecurityKey()))
                .loginAttempts(0)
                .build();
    }

    public void mergeNonNullFields(User user, UserDetailsUpdateRequest userDetailsUpdateRequest) {
        Optional.ofNullable(userDetailsUpdateRequest.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(userDetailsUpdateRequest.getLastName()).ifPresent(user::setLastName);
        Optional.ofNullable(userDetailsUpdateRequest.getPhoneNumber()).ifPresent(user::setPhoneNumber);
        Optional.ofNullable(userDetailsUpdateRequest.getFileId()).ifPresent(user::setProfilePic);
    }
}
