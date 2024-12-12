package com.continuum.cms.service;

import com.continuum.cms.auth.security.JwtService;
import com.continuum.cms.dao.UserDao;
import com.continuum.cms.entity.User;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.util.UserBuilder;
import com.continuum.cms.util.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class LogoutServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private LogoutService logoutService;

    private final User user = UserBuilder.buildDefaultUser("userName", "password", Status.INACTIVE.getName());

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testUpdateLogoutTimeAndActiveStatus() {
        when(jwtService.extractUserName(anyString())).thenReturn("testUser");
        when(userDao.getByUserName(anyString())).thenReturn(Optional.of(user));

        ResponseDto<String> response = logoutService.logout("mockToken");

        assertEquals(0, response.getStatus());
        assertEquals(List.of("User logged out"), response.getData());

    }
}