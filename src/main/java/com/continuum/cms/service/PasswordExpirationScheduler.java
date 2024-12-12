package com.continuum.cms.service;

import com.continuum.cms.dao.UserDao;
import com.continuum.cms.entity.User;
import com.continuum.cms.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class PasswordExpirationScheduler {
    private final UserDao userDao;
    private final DateUtil dateUtil;
    @Scheduled(cron = "${scheduled.cron.time}")
    public void checkPasswordExpiry() {
        log.info("Checking password expiry : {}", "Check expired users");
        List<User> expiredUsers = userDao.getExpiredUser(dateUtil.getPasswordExpiryTime());
        log.info("Found {} expired users.", expiredUsers.size());
        if (!expiredUsers.isEmpty()) {
            processExpiredUsers(expiredUsers);
        } else {
            log.info("No expired users found.");
        }
    }
    public void processExpiredUsers(List<User> expiredUsers) {
        log.info("Processing expired user: {}", "user.getUsername()");
        try {
            userDao.updateUserStatusInBatch(expiredUsers);
            log.info("Expired user {} processed successfully", "user.getUsername()");
        } catch (Exception e) {
            log.error("Error processing expired user {}: {}", "user.getUsername()", e.getMessage());
        }
    }
}

