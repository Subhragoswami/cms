package com.continuum.cms.repository;

import com.continuum.cms.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUserName(String userName);

    Optional<User> findByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.loginAttempts = :loginAttempts, u.lastLogin = :currTime WHERE u.userName = :userName")
    void updateLoginAttemptsByUserName(@Param("loginAttempts") long loginAttempts, @Param("currTime") Date currTime, @Param("userName") String userName);

    @Modifying
    @Query("UPDATE User u SET u.loginAttempts = u.loginAttempts + 1, u.status = CASE WHEN :isBlockStatus = true THEN :blockedStatus ELSE u.status END WHERE u.userName = :userName")
    void updateLoginAttemptsAndStatusByUserName(@Param("isBlockStatus") boolean isBlockStatus, @Param("blockedStatus") String blockedStatus, @Param("userName") String userName);

    @Modifying
    @Query("UPDATE User u SET u.password = :password, u.lastPasswordUpdated = :lastPasswordUpdate, u.status = :status WHERE u.id = :userId")
    void updateUserPassword(UUID userId, String password, Date lastPasswordUpdate, String status);

    @Query("SELECT u FROM User u WHERE u.lastPasswordUpdated < ?1")
    List<User> findExpiredUsers(Date expiryDate);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = :status, u.lastPasswordUpdated = :lastPasswordUpdated  WHERE u IN :users")
    void updateUserStatusInBatch(String status, Date lastPasswordUpdated, List<User> users);

    boolean existsByPhoneNumber(String phoneNumber);
}
