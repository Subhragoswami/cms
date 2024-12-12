package com.continuum.cms.dao;

import com.continuum.cms.auth.security.JwtService;
import com.continuum.cms.config.CMSServiceConfig;
import com.continuum.cms.entity.*;
import com.continuum.cms.mapper.VendorPreferenceMapper;
import com.continuum.cms.model.request.UserDetailsRequest;
import com.continuum.cms.model.request.VendorFilterRequest;
import com.continuum.cms.model.request.VendorRequest;
import com.continuum.cms.repository.*;
import com.continuum.cms.service.EmailService;
import com.continuum.cms.service.FileStorageService;
import com.continuum.cms.specifications.VendorSpecifications;
import com.continuum.cms.util.DateUtil;
import com.continuum.cms.util.EncryptionUtil;
import com.continuum.cms.util.enums.Actions;
import com.continuum.cms.util.enums.Role;
import com.continuum.cms.util.enums.Status;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Slf4j
@RequiredArgsConstructor
public class VendorDetailsDao {
    private final VendorDetailsRepository vendorDetailsRepository;
    private final VendorContactRepository vendorContactRepository;
    private final VendorPreferenceRepository vendorPreferenceRepository;
    private final VendorWhitelistingRepository vendorWhitelistingRepository;
    private final VendorBankDetailsRepository vendorBankDetailsRepository;
    private final FileStorageService fileStorageService;
    private final CMSServiceConfig cmsServiceConfig;
    private final UserDao userDao;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final ModuleDao moduleDao;
    private final VendorUserRepository vendorUserRepository;
    private final VendorPreferenceMapper vendorPreferenceMapper;

    @Transactional
    public void saveDetails(VendorRequest vendorRequest, List<VendorModules> modules, UserRoles userRoles) {
        log.info("Requesting to Creation of Vendor, Request: {}", vendorRequest);
        Vendor savedVendor = vendorDetailsRepository.save(Vendor.fromVendorRequest(vendorRequest));

        VendorContact vendorContact = VendorContact.fromVendorRequest(vendorRequest);
        vendorContact.setVendor(savedVendor);
        vendorContactRepository.save(vendorContact);

        VendorPreference vendorPreference = vendorPreferenceMapper.mapPreferenceDetailsToVendorPreference(vendorRequest.getPreferenceDetails(), VendorPreference.class);
        vendorPreference.setVendor(savedVendor);
        vendorPreferenceRepository.save(vendorPreference);

        VendorBankDetails vendorBankDetails = VendorBankDetails.fromVendorRequest(vendorRequest);
        vendorBankDetails.setVendor(savedVendor);
        vendorBankDetailsRepository.save(vendorBankDetails);
        if(ObjectUtils.isNotEmpty(vendorRequest.getWhitelistingDetails())) {
            VendorWhitelisting vendorWhitelisting = VendorWhitelisting.fromVendorRequest(vendorRequest);
            vendorWhitelisting.setFontFile(vendorRequest.getWhitelistingDetails().getFontFile());
            vendorWhitelisting.setVendor(savedVendor);
            vendorWhitelistingRepository.save(vendorWhitelisting);
        }
        saveVendorAdmin(savedVendor, buildUserObject(vendorRequest.getUserDetails()), userRoles);
        moduleDao.saveVendorModule(buildVendorModule(savedVendor, modules));
        log.info("Vendor creation process finished");
    }

    public List<Vendor> getAllVendors(){
        return vendorDetailsRepository.findAll();
    }
    public Optional<VendorBankDetails> getVendorByCin(String cin) {
        log.info("Fetching vendor by cin {}:", cin);
        return vendorBankDetailsRepository.findByCin(cin);
    }
    public Optional<VendorBankDetails> getVendorByVendorId(UUID vendorId){
        return vendorBankDetailsRepository.findByVendorId(vendorId);
    }

    public Optional<VendorBankDetails> getVendorBankDetailsByVendorId(UUID vendorId) {
        log.info("fetching Vendor Bank Details by vendorId {}", vendorId);
        return vendorBankDetailsRepository.findByVendorId(vendorId);
    }

    public Optional<Vendor> getVendorById(UUID vendorId) {
        return vendorDetailsRepository.findById(vendorId);
    }

    public Vendor saveVendorDetails(Vendor vendor) {
        return vendorDetailsRepository.save(vendor);
    }

    public Optional<UUID> findVendorIdByUserName(String userName) {
        return vendorUserRepository.findVendorIdByUserName(userName);
    }

    public VendorUser findVendorUserDetails(String userName){
        return vendorUserRepository.getVendorDetails(userName);
    }

    public Page<Vendor> getAllVendors(VendorFilterRequest vendorFilterRequest, String search, Pageable pageable) {
        Specification<Vendor> specification = buildSpecification(vendorFilterRequest);
        if (StringUtils.isEmpty(search)) {
            log.info("fetching all users");
            return vendorDetailsRepository.findAll(specification, pageable);
        }
        log.info("fetching users based on the search criteria:{}", search);
        return vendorDetailsRepository.findAll(specification.and(VendorSpecifications.withSearch(search)), pageable);
    }

    public Specification<Vendor> buildSpecification(VendorFilterRequest vendorFilterRequest) {

        Specification<Vendor> specification = Specification.where(null);

        if (ObjectUtils.isNotEmpty(vendorFilterRequest.getStatus())) {
            specification = specification.and(VendorSpecifications.withStatus(vendorFilterRequest.getStatus()));
        }
        if (ObjectUtils.isNotEmpty(vendorFilterRequest.getModules())) {
            specification = specification.and(VendorSpecifications.withModules(vendorFilterRequest.getModules()));
        }

        return specification;
    }

    public Page<Vendor> getVendorsBySearchAndCityAndStatus(String search, String city, String status, Pageable pageable) {
        return vendorDetailsRepository.getVendorsBySearchAndCityAndStatus(search, city, status, pageable);
    }

    public Page<Vendor> getVendorsBySearchAndCity(String search, String city, Pageable pageable) {
        return vendorDetailsRepository.getVendorsBySearchAndCity(search, city, pageable);
    }

    public Page<Vendor> getVendorsBySearchAndStatus(String search, String status, Pageable pageable) {
        return vendorDetailsRepository.getVendorsBySearchAndStatus(search, status, pageable);
    }

    public Page<Vendor> getVendorsBySearch(String search, Pageable pageable) {
        return vendorDetailsRepository.getVendorsBySearch(search, pageable);
    }

    public Page<Vendor> getVendorsByCityAndStatus(String city, String status, Pageable pageable) {
        return vendorDetailsRepository.getVendorsByCityAndStatus(city, status, pageable);
    }

    public Page<Vendor> getVendorsByStatus(String status, Pageable pageable) {
        return vendorDetailsRepository.getVendorsByStatus(status, pageable);
    }

    public Page<Vendor> getVendorsByCity(String city, Pageable pageable) {
        return vendorDetailsRepository.getVendorsByCity(city, pageable);
    }

    public Page<Vendor> getAllVendors(Pageable pageable) {
        return vendorDetailsRepository.findAll(pageable);
    }

    public List<Vendor> getAllByStatus(String status) {
        return vendorDetailsRepository.findAllByStatus(status);
    }

    public Optional<Vendor> findVendorByUserId(UUID userId) {
        log.info("Getting vendor details by userID {}", userId);
        return vendorUserRepository.findVendorByUserId(userId);
    }

    private void saveVendorAdmin(Vendor vendor, User user, UserRoles userRoles) {
        userRoles.setUser(user);
        userDao.createVendorUser(user, vendor, userRoles);
        String token = jwtService.generateJWTToken(user, DateUtils.addHours(DateUtil.getDate(), cmsServiceConfig.getResetTokenExpiryTime()), Role.ROLE_VENDOR_ADMIN.getName());
        userDao.saveUserPasswordAudit(user, token, Status.PENDING, Actions.RESET_PASSWORD);
        emailService.newUserOnboardingEmail(user, token);
    }

    private User buildUserObject(UserDetailsRequest userPostRequest) {
        return User.builder()
                .userName(userPostRequest.getEmail())
                .firstName(userPostRequest.getFirstName())
                .lastName(userPostRequest.getLastName())
                .email(userPostRequest.getEmail())
                .phoneNumber(userPostRequest.getPhoneNumber())
                .status(Status.ACTIVE.name())
                .password(EncryptionUtil.encrypt(userPostRequest.getEmail(), cmsServiceConfig.getAppSecurityKey()))
                .loginAttempts(0)
                .build();
    }

    private List<VendorModules> buildVendorModule(Vendor vendor, List<VendorModules> modules) {
        List<VendorModules> vendorModulesList = new ArrayList<>();
        for(VendorModules vendorModules : modules) {
            vendorModules.setVendor(vendor);
            vendorModulesList.add(vendorModules);
        }
        return vendorModulesList;
    }

}
