package com.continuum.cms.dao;

import com.continuum.cms.entity.UserPasswordAudit;
import com.continuum.cms.repository.UserPasswordAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserPasswordAuditDao {
    private final UserPasswordAuditRepository userPasswordAuditRepository;

    public void saveUserPasswordAudit(UserPasswordAudit userPasswordAuditData) {
        userPasswordAuditRepository.save(userPasswordAuditData);
    }

    public List<UserPasswordAudit> getByUserIdAndStatusAndAction(UUID userId, String status, String action) {
        return userPasswordAuditRepository.findByUserIdAndStatusAndAction(userId, status, action);
    }


}
