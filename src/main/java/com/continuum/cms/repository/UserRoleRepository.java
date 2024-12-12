package com.continuum.cms.repository;

import com.continuum.cms.entity.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRoles, UUID> {

    @Query("SELECT COUNT(ur) FROM UserRoles ur WHERE ur.role.id = :roleId")
    int countByRoleId(UUID roleId);

    long count();
}
