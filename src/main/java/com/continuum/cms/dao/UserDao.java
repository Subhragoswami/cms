package com.continuum.cms.dao;

import com.continuum.cms.config.CMSServiceConfig;
import com.continuum.cms.entity.Roles;
import com.continuum.cms.entity.User;
import com.continuum.cms.entity.UserPasswordAudit;
import com.continuum.cms.entity.UserRoles;
import com.continuum.cms.entity.Vendor;
import com.continuum.cms.entity.VendorUser;
import com.continuum.cms.repository.RolesRepository;
import com.continuum.cms.repository.UserLoginSessionsRepository;
import com.continuum.cms.repository.UserRepository;
import com.continuum.cms.repository.UserRoleRepository;
import com.continuum.cms.repository.VendorUserRepository;
import com.continuum.cms.util.DateUtil;
import com.continuum.cms.util.enums.Actions;
import com.continuum.cms.util.enums.Status;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserDao {

    private final UserRepository userRepository;
    private final CMSServiceConfig cmsServiceConfig;
    private final UserPasswordAuditDao userPasswordAuditDao;
    private final UserLoginSessionsRepository userLoginSessionsRepository;
    private final VendorUserRepository vendorUserRepository;
    private final RolesRepository rolesRepository;
    private final UserRoleRepository userRoleRepository;

    public Optional<User> getByUserName(String userName) {
        log.info("Fetching user details by userName {}:", userName);
        return userRepository.findByUserName(userName);
    }

    public Optional<User> getByEmail(String email) {
        log.info("Fetching user details by email {}:", email);
        return userRepository.findByEmail(email);
    }

    @Transactional
    public void updateLoginAttempt(User user, boolean loginStatus) {
        log.debug("requesting to update LoginAttempt");
        if (loginStatus) {
            userRepository.updateLoginAttemptsByUserName(0, DateUtil.getDate(), user.getUsername());
        } else {
            userRepository.updateLoginAttemptsAndStatusByUserName(cmsServiceConfig.getMaxLoginAttempt() <= user.getLoginAttempts(), Status.BLOCKED.getName(), user.getUsername());
        }
    }

    @Transactional
    public void updateUserPasswordAndAudit(UUID userId, String password, UserPasswordAudit userPasswordAudit) {
        log.debug("requesting to update UserPasswordAndAudit");
        userRepository.updateUserPassword(userId, password, DateUtil.getDate(), Status.ACTIVE.getName());
        userPasswordAudit.setStatus(Status.COMPLETED.getName());
        saveUserPasswordAudit(userPasswordAudit);
    }


    @Transactional
    public void updateUserPasswordAndAudit(User userData, String newPassword) {
        log.debug("requesting to update UserPasswordAudit");
        userRepository.updateUserPassword(userData.getId(), newPassword, DateUtil.getDate(), Status.ACTIVE.getName());
        userPasswordAuditDao.saveUserPasswordAudit(buildUserPasswordAudit(userData));

    }

    public void saveUserPasswordAudit(User user, String token, Status status, Actions action) {
        log.debug("requesting to save UserPasswordAudit");
        userPasswordAuditDao.saveUserPasswordAudit(buildUserPasswordAudit(user, token, status, action));
    }

    public void saveUserPasswordAudit(UserPasswordAudit userPasswordAudit) {
        log.debug("requesting to save UserPasswordAudit");
        userPasswordAuditDao.saveUserPasswordAudit(userPasswordAudit);
    }

    public Optional<Roles> findRoleByName(String roleName) {
        log.debug("requesting to get roles by roleName {}:", roleName);
        return rolesRepository.findByName(roleName);
    }

    @Transactional
    public void updateLogoutTimeAndIsActiveByUserId(UUID userId) {
        log.debug("requesting to update logout time and active status of session  {}", userId);
        userLoginSessionsRepository.updateLogoutTimeAndIsActiveByUserId(DateUtil.getDate(), userId);
    }

    public List<User> getExpiredUser(Date expiryDate) {
        log.debug("Get expired user by expiryDate {}", expiryDate);
        return userRepository.findExpiredUsers(expiryDate);
    }

    public void updateUserStatusInBatch(List<User> user) {
        userRepository.updateUserStatusInBatch(Status.PASSWORD_EXPIRED.getName(), DateUtil.getDate(), user);

    }

    public Optional<User> getUserDetails(String userName) {
        return userRepository.findByUserName(userName);
    }

    public Optional<User> getUserDetailsById(UUID userId) {
        return userRepository.findById(userId);
    }
    public boolean existsByPhoneNumber(String phoneNumber){
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
    public Optional<VendorUser> getUserByUserId(UUID userId){
        log.info("Fetching user by userId {}:", userId);
        return vendorUserRepository.findByUserId(userId);
    }
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    private UserPasswordAudit buildUserPasswordAudit(User userData, String passwordRestToken, Status status, Actions actions) {
        return UserPasswordAudit.builder()
                .userId(userData.getId())
                .status(status.getName())
                .action(actions.getName())
                .requestedBy(userData.getId())
                .passwordResetExpiry(DateUtils.addHours(DateUtil.getDate(), cmsServiceConfig.getResetTokenExpiryTime()))
                .passwordResetToken(passwordRestToken)
                .build();
    }

    private UserPasswordAudit buildUserPasswordAudit(User userData) {
        return UserPasswordAudit.builder()
                .userId(userData.getId())
                .status(Status.COMPLETED.getName())
                .action(Actions.CHANGE_PASSWORD.getName())
                .requestedBy(userData.getId())
                .build();
    }


    @Transactional
    public void createVendorUser(User user, Vendor vendor, UserRoles userRoles) {
        userRepository.save(user);
        userRoleRepository.save(userRoles);
        vendorUserRepository.save(VendorUser.builder().user(user).vendor(vendor).build());
    }

}
