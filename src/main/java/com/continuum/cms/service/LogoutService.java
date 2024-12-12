package com.continuum.cms.service;

import com.continuum.cms.auth.security.JwtService;
import com.continuum.cms.dao.UserDao;
import com.continuum.cms.entity.User;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.util.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogoutService {

    private final JwtService jwtService;
    private final UserDao userDao;

    public ResponseDto<String> logout(String token) {
        String userName = jwtService.extractUserName(token);
        Optional<User> userDetails = userDao.getByUserName(userName);
        userDao.updateLogoutTimeAndIsActiveByUserId(userDetails.get().getId());
        log.info("User logged out");
        return ResponseDto.<String>builder()
                .status(AppConstants.RESPONSE_SUCCESS)
                .data(List.of("User logged out"))
                .build();
    }
}
