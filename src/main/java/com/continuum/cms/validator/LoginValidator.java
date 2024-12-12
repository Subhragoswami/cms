package com.continuum.cms.validator;

import com.continuum.cms.auth.security.JwtService;
import com.continuum.cms.dao.UserLoginSessionDao;
import com.continuum.cms.config.CMSServiceConfig;
import com.continuum.cms.dao.UserDao;
import com.continuum.cms.entity.User;
import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.entity.UserPasswordAudit;
import com.continuum.cms.exceptions.ValidationException;
import com.continuum.cms.model.request.LoginRequest;
import com.continuum.cms.model.request.PasswordChangeRequest;
import com.continuum.cms.model.response.ErrorDto;
import com.continuum.cms.util.*;
import com.continuum.cms.util.enums.Actions;
import lombok.RequiredArgsConstructor;
import com.continuum.cms.util.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.continuum.cms.util.ErrorConstants.*;
import static com.continuum.cms.util.enums.Status.BLOCKED;
import static com.continuum.cms.util.enums.Status.INACTIVE;
import static com.continuum.cms.util.enums.Status.PASSWORD_EXPIRED;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoginValidator {

    private final UserLoginSessionDao userLoginSessionDao;
    private final UserDao userDao;
    private final CMSServiceConfig config;
    private final ValidationUtil validationUtil;
    private final JwtService jwtService;

    public void loginValidator(LoginRequest loginRequest, User user, boolean isLoginRequest) {
        List<ErrorDto> errorMessages = new ArrayList<>();
        if (StringUtils.isEmpty(loginRequest.getUserName())) {
            errorMessages.add(new ErrorDto(MANDATORY_ERROR_CODE, MessageFormat.format(MANDATORY_ERROR_MESSAGE, "UserName")));
        } else if (StringUtils.isEmpty(loginRequest.getPassword())) {
            errorMessages.add(new ErrorDto(MANDATORY_ERROR_CODE, MessageFormat.format(MANDATORY_ERROR_MESSAGE, "password")));
        } else {
            if (INACTIVE.getName().equalsIgnoreCase(user.getStatus())) {
                errorMessages.add(new ErrorDto(USER_IN_ACTIVE_ERROR_CODE, USER_IN_ACTIVE_ERROR_MESSAGE));
            } else if (BLOCKED.getName().equalsIgnoreCase(user.getStatus())) {
                errorMessages.add(new ErrorDto(USER_BLOCKED_ERROR_CODE, USER_BLOCKED_ERROR_MESSAGE));
            } else if (PASSWORD_EXPIRED.getName().equals(user.getStatus())){
                throw new ValidationException(USER_PASSWORD_EXPIRED_ERROR_CODE, MessageFormat.format(USER_PASSWORD_EXPIRED_ERROR_MESSAGE, "Password"));
            }
        }
        if (CollectionUtils.isNotEmpty(errorMessages)) {
            throw new ValidationException(errorMessages);
        }
    }

    public void resetPasswordInitiationValidation(User user,List<UserPasswordAudit> userPasswordAudits) {
        List<ErrorDto> errorMessages = new ArrayList<>();
        validatesUserStatus(user, errorMessages);
        if (CollectionUtils.isNotEmpty(userPasswordAudits)) {
            UserPasswordAudit firstUserPasswordAudit = userPasswordAudits.get(0);
            if (!isEmailRetryTimeExceeded(firstUserPasswordAudit.getPasswordResetToken())) {
                errorMessages.add(new ErrorDto(DUPLICATE_REQUEST_ERROR_CODE, DUPLICATE_REQUEST_ERROR_MESSAGE));
            } else if (ObjectUtils.isNotEmpty(firstUserPasswordAudit)) {
                firstUserPasswordAudit.setStatus(Status.EXPIRED.getName());
                userDao.saveUserPasswordAudit(firstUserPasswordAudit);
            }
        }
        if (CollectionUtils.isNotEmpty(errorMessages)) {
            throw new ValidationException(errorMessages);
        }
    }

    private static void validatesUserStatus(User user, List<ErrorDto> errorMessages) {
        if (INACTIVE.getName().equalsIgnoreCase(user.getStatus())) {
            errorMessages.add(new ErrorDto(USER_IN_ACTIVE_ERROR_CODE, USER_IN_ACTIVE_ERROR_MESSAGE));
        }
    }

    public void duplicateSessionValidator(String username) {
       userLoginSessionDao.getActiveLoginSessionByUserName(username)
                .ifPresent(userLoginSessions -> {
                    throw new CMSException(ErrorConstants.USER_DUPLICATE_SESSION_ERROR_CODE, USER_DUPLICATE_SESSION_ERROR_MESSAGE);
                });
    }

    public void passwordValidation(PasswordChangeRequest passwordChangeRequest, User user, Optional<String> token, Actions actions) {
        validationUtil.validateNotEmpty(passwordChangeRequest.getNewPassword(), "New Password");
        validatePasswordPattern(passwordChangeRequest.getNewPassword());

        validationUtil.validateNotEmpty(passwordChangeRequest.getConfirmPassword(), "Confirm Password");
        validatePasswordPattern(passwordChangeRequest.getConfirmPassword());

        switch (actions) {
            case CHANGE_PASSWORD -> {
                validationUtil.validateNotEmpty(passwordChangeRequest.getCurrentPassword(), "Current Password");
                validatePasswordPattern(passwordChangeRequest.getCurrentPassword());
                if (!decryptPassword(user.getPassword()).equals(decryptPassword(passwordChangeRequest.getCurrentPassword()))) {
                    throw new ValidationException(FIELD_NOT_MATCHING_ERROR_CODE,
                            MessageFormat.format(FIELD_NOT_MATCHING_ERROR_CODE_MESSAGE, "Old Password", "Current Password"));
                }
            }
            case RESET_PASSWORD -> {
                validationUtil.validateNotEmpty(token.get(), "token");
            }
        }

        if (!decryptPassword(passwordChangeRequest.getNewPassword()).equals(decryptPassword(passwordChangeRequest.getConfirmPassword()))) {
            throw new ValidationException(NEW_AND_CONFIRM_PASSWORD_NOT_SAME_ERROR_CODE, NEW_AND_CONFIRM_PASSWORD_NOT_SAME_ERROR_CODE_MASSAGE);
        }
        if (decryptPassword(user.getPassword()).equals(decryptPassword(passwordChangeRequest.getNewPassword()))) {
            throw new ValidationException(OLD_AND_NEW_PASSWORD_SAME_ERROR_CODE, OLD_AND_NEW_PASSWORD_SAME_ERROR_CODE_MESSAGE);

        }
    }

    public void resetPasswordRequestValidation(List<UserPasswordAudit> userPasswordAudits, String token){
        List<ErrorDto> errorMessages = new ArrayList<>();
        validationUtil.validateNotEmpty(userPasswordAudits, "Active Reset Request");
        if(userPasswordAudits.size() > 1) {
            errorMessages.add(new ErrorDto(INVALID_ERROR_CODE, MessageFormat.format(INVALID_ERROR_CODE_MESSAGE, "UserPasswordAudits")));
            userPasswordAudits.forEach(userPasswordAudit -> {userPasswordAudit.setStatus(Status.INACTIVE.getName());
                userDao.saveUserPasswordAudit(userPasswordAudit);
            });
        }  else {
            UserPasswordAudit userPasswordAudit = userPasswordAudits.get(0);
            if(DateUtil.getDate().compareTo(userPasswordAudit.getPasswordResetExpiry()) > 0){
                errorMessages.add(new ErrorDto(EXPIRED_ERROR_CODE, MessageFormat.format(EXPIRED_ERROR_MESSAGE, "Reset Password Request")));
                userPasswordAudit.setStatus(Status.EXPIRED.getName());
                userDao.saveUserPasswordAudit(userPasswordAudit);
            }
            if (!userPasswordAudit.getPasswordResetToken().equals(token)) {
                errorMessages.add(new ErrorDto(INVALID_ERROR_CODE, MessageFormat.format(INVALID_ERROR_CODE_MESSAGE, "Token")));
            }
        }
        if (CollectionUtils.isNotEmpty(errorMessages)) {
            throw new ValidationException(errorMessages);
        }
    }

    private void validatePasswordPattern(String encryptedPassword) {
        if (StringUtils.isNotEmpty(encryptedPassword)) {
            String password = EncryptionUtil.decrypt(encryptedPassword, config.getAppSecurityKey());
            if (!Pattern.matches(AppConstants.PASSWORD_PATTERN, password)) {
                throw new ValidationException(PATTERN_NOT_MATCH_ERROR_CODE, MessageFormat.format(NOT_MATCH_ERROR_MESSAGE_PATTERN, "Password"));
            }
        }
    }
    private String decryptPassword(String encryptedPassword){
        return EncryptionUtil.decrypt(encryptedPassword, config.getAppSecurityKey());
    }

    private boolean isEmailRetryTimeExceeded(String token){
        return System.currentTimeMillis() - jwtService.extractIssuedAt(token) > config.getResetPasswordRetryEmailDuration();
    }

}
