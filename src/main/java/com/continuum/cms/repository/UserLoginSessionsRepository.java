package com.continuum.cms.repository;

import com.continuum.cms.entity.UserLoginSessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public interface UserLoginSessionsRepository extends JpaRepository<UserLoginSessions, UUID> {
     Optional<UserLoginSessions> findByUserUserNameAndIsActiveTrue(String userName);

     @Modifying
     @Query("UPDATE UserLoginSessions u SET u.logoutTime = :currDate, u.isActive = false WHERE u.user.id = :userId")
     void updateLogoutTimeAndIsActiveByUserId(@Param("currDate") Date currDate, UUID userId);
}
