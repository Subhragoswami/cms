package com.continuum.cms.service;

import com.continuum.cms.config.CMSServiceConfig;
import com.continuum.cms.dao.UserLoginSessionDao;
import com.continuum.cms.entity.User;
import com.continuum.cms.entity.UserLoginSessions;
import com.continuum.cms.util.LoginSessionBuilder;
import com.continuum.cms.util.UserBuilder;
import com.continuum.cms.util.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class UserLoginSessionServiceTest {

    @Mock
    private UserLoginSessionDao userLoginSessionDao;

    @Mock
    private CMSServiceConfig cmsServiceConfig;

    @InjectMocks
    private UserLoginSessionService userLoginSessionService;

    private final User user = UserBuilder.buildDefaultUser("userName", "password", Status.INACTIVE.getName());

    private final UserLoginSessions userLoginSessions = LoginSessionBuilder.buildLoginSessionRequest(user);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testSaveUserLoginSessionNewSession() {
        when(userLoginSessionDao.getActiveLoginSessionByUserName(Mockito.anyString())).thenReturn(Optional.of(userLoginSessions));

        userLoginSessionService.saveUserLoginSession(user);

        verify(userLoginSessionDao, times(1)).getActiveLoginSessionByUserName(anyString());
        verify(userLoginSessionDao, times(1)).updateUserLoginSession(any(UserLoginSessions.class));
        verify(userLoginSessionDao, times(1)).saveUserLoginSession(any(UserLoginSessions.class));

    }

    @Test
    public void testSaveUserLoginSessionExistingSession() {
        when(userLoginSessionDao.getActiveLoginSessionByUserName(anyString())).thenReturn(Optional.empty());

        userLoginSessionService.saveUserLoginSession(user);

        verify(userLoginSessionDao, times(1)).getActiveLoginSessionByUserName(anyString());
        verify(userLoginSessionDao, times(0)).updateUserLoginSession(any(UserLoginSessions.class));
        verify(userLoginSessionDao, times(1)).saveUserLoginSession(any(UserLoginSessions.class));
    }

}