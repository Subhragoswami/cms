package com.continuum.cms.repository;


import com.continuum.cms.entity.UserPasswordAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserPasswordAuditRepository extends JpaRepository<UserPasswordAudit, UUID> {
    @Query("SELECT upa FROM UserPasswordAudit upa WHERE upa.userId = :userId AND upa.status = :status AND upa.action = :action")
    List<UserPasswordAudit> findByUserIdAndStatusAndAction(UUID userId, String status, String action);

}
