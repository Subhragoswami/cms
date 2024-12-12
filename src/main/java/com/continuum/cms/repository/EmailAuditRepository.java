package com.continuum.cms.repository;


import com.continuum.cms.entity.EmailAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmailAuditRepository extends JpaRepository<EmailAudit, UUID> {
}
