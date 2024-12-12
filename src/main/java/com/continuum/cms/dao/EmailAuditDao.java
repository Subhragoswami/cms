package com.continuum.cms.dao;

import com.continuum.cms.entity.EmailAudit;
import com.continuum.cms.repository.EmailAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;


@Repository
@Slf4j
@RequiredArgsConstructor
public class EmailAuditDao {

    private final EmailAuditRepository emailAuditRepository;
    public void saveEmailAudit(EmailAudit emailAudit){
        emailAuditRepository.save(emailAudit);
    }
}
