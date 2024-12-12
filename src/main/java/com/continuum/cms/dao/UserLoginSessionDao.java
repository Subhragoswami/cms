package com.continuum.cms.dao;

import com.continuum.cms.repository.UserLoginSessionsRepository;
import com.continuum.cms.entity.UserLoginSessions;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserLoginSessionDao {

    private final UserLoginSessionsRepository userLoginSessionsRepository;

    @Transactional
    public void saveUserLoginSession(UserLoginSessions userLoginSessions) {
        log.debug("requesting to add login session in DB {}", userLoginSessions);
        userLoginSessionsRepository.save(userLoginSessions);
    }

    public Optional<UserLoginSessions> getActiveLoginSessionByUserName(String userName) {
        log.debug("requesting to get login session by userName {}", userName);
        return userLoginSessionsRepository.findByUserUserNameAndIsActiveTrue(userName);
    }

    public void updateUserLoginSession(UserLoginSessions userLoginSessions) {
        log.debug("requesting to update login session in DB {}", userLoginSessions);
        userLoginSessionsRepository.save(userLoginSessions);
    }
}
