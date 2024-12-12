package com.continuum.cms.validator;

import com.continuum.cms.dao.UserLoginSessionDao;
import com.continuum.cms.entity.User;
import com.continuum.cms.entity.UserLoginSessions;
import com.continuum.cms.util.LoginSessionBuilder;
import com.continuum.cms.util.UserBuilder;
import com.continuum.cms.util.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class LoginValidatorTest {

    @Mock
    UserLoginSessionDao userLoginSessionDao;

    @InjectMocks
    private LoginValidator loginValidator;

    User user = UserBuilder.buildDefaultUser("userName", "password", Status.INACTIVE.getName());
    private final UserLoginSessions userLoginSessions = LoginSessionBuilder.buildLoginSessionRequest(user);

    @BeforeEach
    public void setUp() {

    }

//    @Test
//    public void testLoginRequestValidationValidUser() {
//        LoginRequest loginRequest = LoginRequestBuilder.buildLoginRequest("userName", "password");
//
//        assertDoesNotThrow(() -> loginValidator.loginValidator(loginRequest, user,true));
//    }
//
//    @Test
//    public void testLoginRequestValidationInvalidUserName() {
//        LoginRequest loginRequest = LoginRequestBuilder.buildLoginRequest("", "password");
//        ValidationException exception = assertThrows(ValidationException.class, () -> loginValidator.loginValidator(loginRequest, user, true));
//        List<ErrorDto> errorMessages = exception.getErrorMessages();
//
//        assertEquals(1, errorMessages.size());
//        assertEquals(ErrorConstants.MANDATORY_ERROR_CODE, errorMessages.get(0).getErrorCode());
//    }
//
//    @Test
//    public void testRequestValidationInvalidPassword() {
//        LoginRequest loginRequest = new LoginRequest("testUser", "");
//
//        ValidationException exception = assertThrows(ValidationException.class, () -> loginValidator.loginValidator(loginRequest, user, true));
//        List<ErrorDto> errorMessages = exception.getErrorMessages();
//        assertEquals(1, errorMessages.size());
//        assertEquals(ErrorConstants.MANDATORY_ERROR_CODE, errorMessages.get(0).getErrorCode());
//    }
//
//    @Test
//    public void testRequestValidationInactiveUser() {
//        LoginRequest loginRequest = new LoginRequest("testUser", "password");
//        user.setStatus(Status.INACTIVE.getName());
//
//        ValidationException exception = assertThrows(ValidationException.class, () -> loginValidator.loginValidator(loginRequest, user, true));
//        List<ErrorDto> errorMessages = exception.getErrorMessages();
//        assertEquals(1, errorMessages.size());
//        assertEquals(ErrorConstants.USER_IN_ACTIVE_ERROR_CODE, errorMessages.get(0).getErrorCode());
//    }
//
//    @Test
//    public void testRequestValidationBlockedUser() {
//        LoginRequest loginRequest = new LoginRequest("testUser", "password");
//        user.setStatus(Status.BLOCKED.getName());
//
//        ValidationException exception = assertThrows(ValidationException.class, () -> loginValidator.loginValidator(loginRequest, user, true));
//        List<ErrorDto> errorMessages = exception.getErrorMessages();
//        assertEquals(1, errorMessages.size());
//        assertEquals(ErrorConstants.USER_BLOCKED_ERROR_CODE, errorMessages.get(0).getErrorCode());
//    }
//
//    @Test
//    public void testMultiLoginValidatorDuplicateSession() {
//        when(userLoginSessionDao.getActiveLoginSessionByUserName(anyString())).thenReturn(Optional.of(userLoginSessions));
//
//        CMSException exception = assertThrows(CMSException.class, () -> loginValidator.duplicateSessionValidator(user.getUsername()));
//
//        assertEquals(exception.getErrorMessage(), ErrorConstants.USER_DUPLICATE_SESSION_ERROR_MESSAGE);
//        assertEquals(exception.getErrorCode(), ErrorConstants.USER_DUPLICATE_SESSION_ERROR_CODE);
//    }
//
//    @Test
//    public void testMultiLoginValidatorNoDuplicateSession() {
//        when(userLoginSessionDao.getActiveLoginSessionByUserName(anyString())).thenReturn(Optional.empty());
//
//        assertDoesNotThrow(() -> loginValidator.duplicateSessionValidator(user.getUsername()));
//    }
}
