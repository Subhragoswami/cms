package com.continuum.cms.service;

import com.continuum.cms.auth.security.AuthenticationService;
import com.continuum.cms.auth.security.JwtService;
import com.continuum.cms.config.CMSServiceConfig;
import com.continuum.cms.dao.UserDao;
import com.continuum.cms.dao.UserPasswordAuditDao;
import com.continuum.cms.entity.User;
import com.continuum.cms.entity.UserPasswordAudit;
import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.model.request.LoginRequest;
import com.continuum.cms.model.request.PasswordChangeRequest;
import com.continuum.cms.model.response.LoginResponse;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.util.AppConstants;
import com.continuum.cms.util.EncryptionUtil;
import com.continuum.cms.util.ErrorConstants;
import com.continuum.cms.util.enums.Actions;
import com.continuum.cms.util.enums.Status;
import com.continuum.cms.validator.LoginValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import static com.continuum.cms.util.AppConstants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginService {

    private final LoginValidator loginValidator;

    private final AuthenticationService authenticationService;
    private final UserLoginSessionService userLoginSessionService;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final CMSServiceConfig cmsServiceConfig;

    private final UserPasswordAuditDao userPasswordAuditDao;
    private final UserDao userDao;

    public LoginResponse login(LoginRequest loginRequest, boolean isLoginRequest) {
        User user = userDao.getByUserName(loginRequest.getUserName()).orElseThrow(() -> new CMSException(ErrorConstants.NOT_FOUND_ERROR_CODE, MessageFormat.format(ErrorConstants.NOT_FOUND_ERROR_MESSAGE, "User")));
        loginValidator.loginValidator(loginRequest, user, isLoginRequest);
        if (!(EncryptionUtil.decrypt(loginRequest.getPassword(), cmsServiceConfig.getAppSecurityKey()).equalsIgnoreCase(EncryptionUtil.decrypt(user.getPassword(), cmsServiceConfig.getAppSecurityKey())))) {
            userDao.updateLoginAttempt(user, false);
            throw new CMSException(ErrorConstants.INVALID_ERROR_CODE, MessageFormat.format(ErrorConstants.INVALID_ERROR_CODE_MESSAGE, "Password"));
        }
        if(isLoginRequest) {
            loginValidator.duplicateSessionValidator(user.getUsername());
        }
        userDao.updateLoginAttempt(user, true);
        String token = authenticationService.generateJwtToken(user);
        log.debug("Received request for login {} and generated token {}", loginRequest, token);
        userLoginSessionService.saveUserLoginSession(user);
        return LoginResponse.builder().status(AppConstants.RESPONSE_SUCCESS).token(token).build();
    }

    public ResponseDto<String> initiateResetPassword(String userName) {
        User user = userDao.getByUserName(userName).orElseThrow(() -> new CMSException(ErrorConstants.NOT_FOUND_ERROR_CODE, MessageFormat.format(ErrorConstants.NOT_FOUND_ERROR_MESSAGE, "User")));
        List<UserPasswordAudit> userPasswordAudits = userPasswordAuditDao.getByUserIdAndStatusAndAction(user.getId(), Status.PENDING.getName(), Actions.RESET_PASSWORD.getName());
        loginValidator.resetPasswordInitiationValidation(user, userPasswordAudits);
        String token = authenticationService.generateResetPasswordJwtToken(user);
        userDao.saveUserPasswordAudit(user, token, Status.PENDING, Actions.RESET_PASSWORD);
        emailService.resetPasswordInitiateEmail(user.getEmail(), user, token);
        return ResponseDto.<String>builder().status(RESPONSE_SUCCESS).data(List.of(PASSWORD_RESET_INITIATION_SUCCESS_MESSAGE)).build();
    }

    public ResponseDto<String> resetPassword( String token, PasswordChangeRequest passwordChangeRequest) {
        String userName = jwtService.extractUserName(token);
        User user = userDao.getByUserName(userName).orElseThrow(() -> new CMSException(ErrorConstants.NOT_FOUND_ERROR_CODE, MessageFormat.format(ErrorConstants.NOT_FOUND_ERROR_MESSAGE, "User")));
        loginValidator.passwordValidation(passwordChangeRequest, user, Optional.of(token), Actions.RESET_PASSWORD);
        List<UserPasswordAudit> userPasswordAudits = userPasswordAuditDao.getByUserIdAndStatusAndAction(user.getId(), Status.PENDING.getName(), Actions.RESET_PASSWORD.getName());
        loginValidator.resetPasswordRequestValidation(userPasswordAudits, token);
        userDao.updateUserPasswordAndAudit(user.getId(), passwordChangeRequest.getConfirmPassword(), userPasswordAudits.get(0));
        emailService.resetPasswordUpdatedEmail(user.getEmail(), user);
        return ResponseDto.<String>builder().status(RESPONSE_SUCCESS).data(List.of(PASSWORD_RESET_SUCCESS_MESSAGE)).build();
    }

    public ResponseDto<String> changePassword(String token, PasswordChangeRequest passwordChangeRequest){
        String userName = jwtService.extractUserName(token);
        User user = userDao.getByUserName(userName).orElseThrow(() -> new CMSException(ErrorConstants.NOT_FOUND_ERROR_CODE, MessageFormat.format(ErrorConstants.NOT_FOUND_ERROR_MESSAGE, "User")));
        loginValidator.passwordValidation(passwordChangeRequest, user, Optional.empty(), Actions.CHANGE_PASSWORD);
        userDao.updateUserPasswordAndAudit(user, passwordChangeRequest.getNewPassword());
        emailService.changePasswordConfirmationEmail(user.getEmail(),user);
        return ResponseDto.<String>builder().status(RESPONSE_SUCCESS).data(List.of(PASSWORD_CHANGE_SUCCESS_MESSAGE)).build();
    }

}


