package com.continuum.cms.service;

import com.continuum.cms.entity.User;
import com.continuum.cms.dao.UserLoginSessionDao;
import com.continuum.cms.entity.UserLoginSessions;
import com.continuum.cms.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserLoginSessionService {

    private final UserLoginSessionDao userLoginSessionDao;

    public void saveUserLoginSession(User user) {
        log.info("Requesting to save the data for User login session {}", user);
        userLoginSessionDao.getActiveLoginSessionByUserName(user.getUsername())
                .ifPresent(userLoginSessions -> {
                    userLoginSessions.setActive(false);
                    userLoginSessions.setLogoutTime(new Date());
                    userLoginSessionDao.updateUserLoginSession(userLoginSessions);
                });
        userLoginSessionDao.saveUserLoginSession(buildUserLoginSession(user));
    }

    private UserLoginSessions buildUserLoginSession(User user) {
        log.info("building a instance for User login session {}", user);
        return UserLoginSessions.builder()
                .user(user)
                .loginTime(DateUtil.getDate())
                .isActive(true)
                .build();
    }
}
