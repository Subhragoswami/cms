package com.continuum.cms.service;

import com.amazonaws.services.cloudtrail.model.InvalidTokenException;
import com.continuum.cms.auth.security.AuthenticationService;
import com.continuum.cms.auth.security.JwtService;
import com.continuum.cms.config.CMSServiceConfig;
import com.continuum.cms.dao.UserDao;
import com.continuum.cms.dao.UserPasswordAuditDao;
import com.continuum.cms.entity.User;
import com.continuum.cms.entity.UserPasswordAudit;
import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.exceptions.ValidationException;
import com.continuum.cms.model.request.LoginRequest;
import com.continuum.cms.model.request.PasswordChangeRequest;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.util.*;
import com.continuum.cms.util.enums.Status;
import com.continuum.cms.validator.LoginValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static com.continuum.cms.util.AppConstants.RESPONSE_SUCCESS;
import static org.junit.jupiter.api.Assertions.*;
import static com.continuum.cms.util.AppConstants.PASSWORD_CHANGE_SUCCESS_MESSAGE;
import static org.mockito.Mockito.*;

public class LoginServiceTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private LoginValidator loginValidator;

    @Mock
    private UserLoginSessionService userLoginSessionService;

    @Mock
    private CMSServiceConfig cmsServiceConfig;
    @Mock
    private UserPasswordAuditDao userPasswordAuditDao;
    @Mock
    private UserDao userDao;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtService jwtService;


    @InjectMocks
    private LoginService loginService;

    private final LoginRequest loginRequest = LoginRequestBuilder.buildLoginRequest("userName", "password");
    private final User user = UserBuilder.buildDefaultUser("userName", "password", Status.INACTIVE.getName());
    private final UserPasswordAudit userPasswordAudit = UserPasswordAuditBuilder.buildUserPasswordAuditRequest("active");

    private final UserPasswordAudit passwordAudit = UserPasswordAuditBuilder.buildUserPasswordAuditRequest("Active");
    private final PasswordChangeRequest passwordChangeRequest =
            buildPasswordChangeRequestBuilder("userName","Example@09","Example@09","Example@09");

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

//    @Test
//    public void testLoginWithValidCredentials() {
//        // Mocking dependencies
//        LoginRequest loginRequest = new LoginRequest("username", "password");
//        User user = new User(); // create a user object with valid credentials
//        when(userDao.getByUserName(anyString())).thenReturn(Optional.of(user));
//        when(cmsServiceConfig.getAppSecurityKey()).thenReturn("securityKey");
//        when(authenticationService.generateJwtToken(any(User.class))).thenReturn("mockedToken");
//
//        // Execute the method under test
//        LoginResponse response = loginService.login(loginRequest, true);
//
//        // Assertions
//        assertEquals(AppConstants.RESPONSE_SUCCESS, response.getStatus());
//        assertEquals("mockedToken", response.getToken());
//        assertNotNull(response.getToken());
//
//        // Verify interactions with mocks
//        verify(userDao, times(1)).getByUserName("username");
//        verify(loginValidator, times(1)).loginValidator(loginRequest, user, true);
//        verify(userDao, times(1)).updateLoginAttempt(user, true);
//        verify(authenticationService, times(1)).generateJwtToken(user);
//        verify(userLoginSessionService, times(1)).saveUserLoginSession(user);
//        LoginRequest loginRequest = new LoginRequest("userName", "password");
//        User user = new User();
//        user.setUserName("userName");
//        user.setPassword(EncryptionUtil.encrypt("password", "continuum"));
//        when(userDao.getByUserName(user.getUsername())).thenReturn(Optional.of(user));
//        when(EncryptionUtil.decrypt(loginRequest.getPassword(),"continuum" )).thenReturn("password");
//        when(authenticationService.generateJwtToken(user)).thenReturn("testToken");
//
//        LoginResponse response = loginService.login(loginRequest, true);
//
//        Assertions.assertEquals(AppConstants.RESPONSE_SUCCESS, response.getStatus());
//        Assertions.assertNotNull(response.getToken());
//        Mockito.verify(userLoginSessionService).saveUserLoginSession(user);
//    }

//    @Test
//    public void testLoginWithInValidCredentials() {
//        loginRequest.setPassword("123456");
//        when(userDao.getByUserName(anyString())).thenReturn(Optional.of(user));
//
//        CMSException exception = assertThrows(CMSException.class, () -> loginService.login(loginRequest, true));
//        assertEquals(ErrorConstants.INVALID_ERROR_CODE, exception.getErrorCode());
//
//        verify(loginValidator, times(1)).loginValidator(any(LoginRequest.class), any(User.class), anyBoolean());
//        verify(loginValidator, times(0)).duplicateSessionValidator(user.getUsername());
//        verify(authenticationService, times(0)).generateJwtToken(any(User.class));
//        verify(userLoginSessionService, times(0)).saveUserLoginSession(any(User.class));
//        verify(userDao, times(1)).updateLoginAttempt(any(User.class), anyBoolean());
//    }

    @Test
    public void testLoginWithNoUserFound() {
        when(userDao.getByUserName(anyString())).thenReturn(Optional.empty());

        CMSException exception = assertThrows(CMSException.class, () -> loginService.login(loginRequest, true));
        assertEquals(ErrorConstants.NOT_FOUND_ERROR_CODE, exception.getErrorCode());

        verify(loginValidator, Mockito.times(0)).loginValidator(any(LoginRequest.class), any(User.class), anyBoolean());
        verify(loginValidator, Mockito.times(0)).duplicateSessionValidator(user.getUsername());
        verify(authenticationService, Mockito.times(0)).generateJwtToken(any(User.class));
        verify(userLoginSessionService, Mockito.times(0)).saveUserLoginSession(any(User.class));
        verify(userDao, times(0)).updateLoginAttempt(any(User.class), anyBoolean());
    }

//    @Test
//    public void testReLoginWithValidCredentials() {
//        when(userDao.getByUserName(Mockito.anyString())).thenReturn(Optional.of(user));
//        when(authenticationService.generateJwtToken(any(User.class))).thenReturn("mockedToken");
//
//        LoginResponse response = loginService.login(loginRequest, false);
//
//        assertEquals(RESPONSE_SUCCESS, response.getStatus());
//        assertEquals("mockedToken", response.getToken());
//        assertNotNull(response.getToken());
//
//        verify(loginValidator, times(1)).loginValidator(any(LoginRequest.class), any(User.class), anyBoolean());
//        verify(loginValidator, times(0)).duplicateSessionValidator(user.getUsername());
//        verify(authenticationService, times(1)).generateJwtToken(any(User.class));
//        verify(userLoginSessionService, times(1)).saveUserLoginSession(any(User.class));
//        verify(userDao, times(1)).updateLoginAttempt(any(User.class), anyBoolean());
//    }

//    @Test
//    public void testReLoginWithInValidCredentials() {
//        loginRequest.setPassword("123456");
//        when(userDao.getByUserName(Mockito.anyString())).thenReturn(Optional.of(user));
//
//        CMSException exception = assertThrows(CMSException.class, () -> loginService.login(loginRequest, false));
//        assertEquals(ErrorConstants.NOT_VALID_ERROR_CODE, exception.getErrorCode());
//
//        verify(loginValidator, times(1)).loginValidator(any(LoginRequest.class), any(User.class), anyBoolean());
//        verify(loginValidator, times(0)).duplicateSessionValidator(user.getUsername());
//        verify(authenticationService, times(0)).generateJwtToken(any(User.class));
//        verify(userLoginSessionService, times(0)).saveUserLoginSession(any(User.class));
//        verify(userDao, times(1)).updateLoginAttempt(any(User.class), anyBoolean());
//    }

    @Test
    public void testReLoginWithNoUserFound() {
        when(userDao.getByUserName(anyString())).thenReturn(Optional.empty());

        CMSException exception = assertThrows(CMSException.class, () -> loginService.login(loginRequest, false));
        assertEquals(ErrorConstants.NOT_FOUND_ERROR_CODE, exception.getErrorCode());

        Mockito.verify(loginValidator, Mockito.times(0)).loginValidator(any(LoginRequest.class), any(User.class), anyBoolean());
        Mockito.verify(loginValidator, Mockito.times(0)).duplicateSessionValidator(user.getUsername());
        Mockito.verify(authenticationService, Mockito.times(0)).generateJwtToken(Mockito.any(User.class));
        Mockito.verify(userLoginSessionService, Mockito.times(0)).saveUserLoginSession(Mockito.any(User.class));
        Mockito.verify(userDao, Mockito.times(0)).updateLoginAttempt(Mockito.any(User.class), Mockito.anyBoolean());
    }

    @Test
    public void testResetPasswordInitiationOfValidUser(){
        user.setStatus(Status.ACTIVE.getName());
        when(userDao.getByUserName(anyString())).thenReturn(Optional.of(user));
        when(userPasswordAuditDao.getByUserIdAndStatusAndAction(any(), anyString(), anyString() )).thenReturn(List.of(userPasswordAudit));

        ResponseDto<String> response = loginService.initiateResetPassword(loginRequest.getUserName());

        assertEquals(RESPONSE_SUCCESS, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertEquals("Password Request Initiation Completed.", response.getData().get(0));

        verify(loginValidator,times(1)).resetPasswordInitiationValidation(any(),any());
        verify(authenticationService,times(1)).generateResetPasswordJwtToken(any(User.class));
        verify(userDao,times(1)).saveUserPasswordAudit(any(),any(),any(), any());
        verify(emailService,times(1)).resetPasswordInitiateEmail(any(),any(), any());
    }


    @Test
    public void testChangePasswordSuccess() {
        String token = "validToken";

        when(jwtService.extractUserName(token)).thenReturn(user.getUsername());
        when(userDao.getByUserName(user.getUsername())).thenReturn(Optional.of(user));

        ResponseDto<String> response = loginService.changePassword(token, passwordChangeRequest);
        assertNotNull(response);

        assertEquals(RESPONSE_SUCCESS, response.getStatus());
        assertEquals(PASSWORD_CHANGE_SUCCESS_MESSAGE, response.getData().get(0));
        verify(jwtService,times(1)).extractUserName(any());
        //verify(userDao,times(1)).updateUserPasswordAndAudit(any(UUID.class),anyString(), any());
        verify(emailService,times(1)).changePasswordConfirmationEmail(any(),any());

    }
    @Test
    void testChangePasswordUserNotFound() {
        String token = "validToken";

        when(jwtService.extractUserName(token)).thenReturn(user.getUsername());
        when(userDao.getByUserName(user.getUsername())).thenReturn(Optional.empty());

        assertThrows(CMSException.class, () -> loginService.changePassword(token, passwordChangeRequest));
    }
    @Test
    void testChangePasswordInvalidToken() {
        String invalidToken = null;
        when(jwtService.extractUserName(invalidToken)).thenThrow(new InvalidTokenException("Invalid token"));

        assertThrows(InvalidTokenException.class, () -> loginService.changePassword(invalidToken, passwordChangeRequest));
    }
    @Test
    public void testChangePasswordInvalidUsernameInToken() {
        String invalidToken = "invalidToken";
        when(jwtService.extractUserName(invalidToken)).thenReturn("nonExistingUser");
        assertThrows(CMSException.class, () -> loginService.changePassword(invalidToken, passwordChangeRequest));
    }
    @Test
    public void testChangePasswordInvalidNewPasswordFormat() {
        String token = "validToken";
        String invalidNewPassword = "shortpwd";
        passwordChangeRequest.setNewPassword(invalidNewPassword);

        when(jwtService.extractUserName(token)).thenReturn(user.getUsername());
        when(userDao.getByUserName(user.getUsername())).thenReturn(Optional.of(user));

        doThrow(ValidationException.class).when(loginValidator).passwordValidation(any(), any(), any(), any());
        assertThrows(ValidationException.class, () -> loginService.changePassword(token, passwordChangeRequest));
    }

    private static PasswordChangeRequest buildPasswordChangeRequestBuilder(String username, String currentPassword, String newPassword , String confirmPassword){
        return PasswordChangeRequest.builder()
                .currentPassword(currentPassword)
                .newPassword(newPassword)
                .confirmPassword(confirmPassword)
                .build();
    }

}

