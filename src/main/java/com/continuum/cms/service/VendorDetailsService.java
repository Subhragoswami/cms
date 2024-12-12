
package com.continuum.cms.service;

import com.continuum.cms.dao.*;
import com.continuum.cms.entity.*;
import com.continuum.cms.entity.Module;
import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.exceptions.ValidationException;
import com.continuum.cms.mapper.VendorMapper;
import com.continuum.cms.model.request.*;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.model.response.VendorDetailsResponse;
import com.continuum.cms.model.response.VendorFilterListResponse;
import com.continuum.cms.model.response.VendorPostResponse;
import com.continuum.cms.model.response.WhitelistingDetailsResponse;
import com.continuum.cms.util.ErrorConstants;
import com.continuum.cms.validator.UserValidator;
import com.continuum.cms.validator.VendorDetailsValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.continuum.cms.util.AppConstants.RESPONSE_FAILURE;
import static com.continuum.cms.util.AppConstants.RESPONSE_SUCCESS;
import static com.continuum.cms.util.ErrorConstants.*;
import static com.continuum.cms.util.enums.Role.ROLE_VENDOR_ADMIN;

@Service
@Slf4j
@RequiredArgsConstructor
public class VendorDetailsService {

    private final VendorDetailsValidator vendorDetailsValidator;
    private final VendorDetailsDao vendorDetailsDao;
    private final VendorContactDao vendorContactDao;
    private final VendorPreferenceDao vendorPreferenceDao;
    private final VendorWhiteListingDao vendorWhiteListingDao;
    private final VendorBankDao vendorBankDao;
    private final FileStorageService fileStorageService;
    private final UserValidator userValidator;
    private final UserDao userDao;
    private final ModuleDao moduleDao;
    private final ObjectMapper mapper;
    private final VendorMapper vendorMapper;

    private Map<String, String> vendorCINToEndpoints;

    @PostConstruct
    private void init() {
        loadVendorCINToEndpointMap();
    }

    public ResponseDto<String> addVendor(VendorRequest vendorRequest) {
        userValidator.validateUserDetailsRequest(vendorRequest.getUserDetails(), ROLE_VENDOR_ADMIN);
        vendorDetailsValidator.validatePanAndGST(vendorRequest.getBankDetailsRequest().getPan(), vendorRequest.getBankDetailsRequest().getGst());
        log.info("Starting Creation of Vendor, Request: {}", vendorRequest);
        userDao.getByEmail(vendorRequest.getUserDetails().getEmail()).ifPresent(user -> {
            throw new ValidationException(FIELD_ALREADY_EXIST_ERROR_CODE,
                    MessageFormat.format(FIELD_ALREADY_EXIST_ERROR_CODE_MESSAGE, "User"));
        });
        vendorDetailsDao.getVendorByCin(vendorRequest.getBankDetailsRequest().getCin()).ifPresent(v -> {
            throw new ValidationException(FIELD_ALREADY_EXIST_ERROR_CODE,
                    MessageFormat.format(FIELD_ALREADY_EXIST_ERROR_CODE_MESSAGE, "CIN"));
        });
        Roles role = userDao.findRoleByName(ROLE_VENDOR_ADMIN.getName()).orElseThrow(
                () -> new CMSException(NOT_FOUND_ERROR_CODE, MessageFormat.format(NOT_FOUND_ERROR_MESSAGE,"Role")));
        vendorDetailsValidator.requestValidation(vendorRequest);
        UserRoles userRoles = UserRoles.builder()
                .role(role)
                .build();
        vendorDetailsDao.saveDetails(vendorRequest, buildModule(vendorRequest.getModules()), userRoles);
        loadVendorCINToEndpointMap();
        return ResponseDto.<String>builder()
                .status(RESPONSE_SUCCESS)
                .data(List.of("vendor added successfully"))
                .build();
    }

    public ResponseDto<String> updateVendorDetails(UUID vendorId, VendorUpdateRequest vendorRequest) {
        boolean isDataUpdated = false;
        log.info("Starting Updation of Vendor, Request: {}", vendorRequest);
        Vendor vendorDB = vendorDetailsDao.getVendorById(vendorId).orElseThrow(() -> new ValidationException(INVALID_ERROR_CODE,
                MessageFormat.format(INVALID_ERROR_CODE_MESSAGE, "Vendor Id")));

        VendorContact vendorContactDB = vendorContactDao.getVendorContactByVendorId(vendorDB.getId()).orElseThrow(() -> new ValidationException(INVALID_ERROR_CODE,
                MessageFormat.format(INVALID_ERROR_CODE_MESSAGE, "Vendor Id")));

        VendorPreference vendorPreferenceDB = vendorPreferenceDao.getVendorPreferenceByVendorId(vendorDB.getId()).orElseThrow(() -> new ValidationException(INVALID_ERROR_CODE,
                MessageFormat.format(INVALID_ERROR_CODE_MESSAGE, "Vendor Id")));

        VendorBankDetails vendorBankDB = vendorBankDao.getVendorBankByVendorId(vendorDB.getId()).orElseThrow(() -> new ValidationException(INVALID_ERROR_CODE,
                MessageFormat.format(INVALID_ERROR_CODE_MESSAGE, "Vendor Id")));

        Optional<VendorWhitelisting> vendorWhitelistingOptional = vendorWhiteListingDao.getVendorWhiteListingByVendorId(vendorDB.getId());

        if (isVendorBasicDataUpdated(vendorDB, vendorRequest)) {
            vendorDetailsDao.saveVendorDetails(vendorDB);
            isDataUpdated = true;
        }

        if(isVendorContactDetailsUpdated(vendorContactDB,vendorRequest.getContactDetails())) {
            vendorContactDao.saveVendorContactDetails(vendorContactDB);
            isDataUpdated = true;
        }

        if(isVendorPreferenceUpdated(vendorPreferenceDB, vendorRequest.getPreferenceDetails())) {
            vendorPreferenceDao.saveVendorPreference(vendorPreferenceDB);
            loadVendorCINToEndpointMap();
            isDataUpdated = true;
        }

        if(isVendorBankDetailsUpdated(vendorBankDB, vendorRequest.getBankDetailsRequest())) {
            vendorBankDao.saveVendorBankingDetails(vendorBankDB);
            isDataUpdated = true;
        }

        if (vendorWhitelistingOptional.isPresent()) {
            if (ObjectUtils.isNotEmpty(vendorRequest.getWhitelistingDetails()) && isVendorWhiteListingDetailsUpdated(vendorWhitelistingOptional.get(), vendorRequest.getWhitelistingDetails())) {
                vendorWhiteListingDao.saveVendorWhiteListingDetails(vendorWhitelistingOptional.get());
                isDataUpdated = true;
            }
        } else {
            VendorWhitelisting vendorWhitelisting = new VendorWhitelisting();
            if (ObjectUtils.isNotEmpty(vendorRequest.getWhitelistingDetails()) && isVendorWhiteListingDetailsUpdated(vendorWhitelisting, vendorRequest.getWhitelistingDetails())) {
                vendorWhitelisting.setVendor(vendorDB);
                vendorWhiteListingDao.saveVendorWhiteListingDetails(vendorWhitelisting);
                isDataUpdated = true;
            }
        }

        if (ObjectUtils.isNotEmpty(vendorRequest.getModules())) {
            moduleDao.saveVendorModule(buildVendorModule(vendorDB, vendorRequest.getModules()));
            isDataUpdated = true;
        }

        if( isDataUpdated ) {
            return ResponseDto.<String>builder()
                    .status(RESPONSE_SUCCESS)
                    .data(List.of("vendor updated successfully"))
                    .build();
        }
        return ResponseDto.<String>builder().status(RESPONSE_FAILURE)
                .data(List.of("No Vendor Data to Update")).build();

    }

    public ResponseDto<VendorPostResponse> getVendorDetailsByUserName(String userName) {
        log.info("Starting process to get Vendor, : {}", userName);
        UUID vendorId = vendorDetailsDao.findVendorIdByUserName(userName).orElseThrow(() -> new ValidationException(INVALID_ERROR_CODE,
                MessageFormat.format(INVALID_ERROR_CODE_MESSAGE, "UserName")));
        return getVendorDetailById(vendorId);
    }

    public ResponseDto<VendorPostResponse> getVendorDetailById(UUID vendorId) {
        log.info("Starting process to get Vendor by vendor ID, : {}", vendorId);
        Vendor vendorDB = vendorDetailsDao.getVendorById(vendorId).orElseThrow(() -> new ValidationException(INVALID_ERROR_CODE,
                MessageFormat.format(INVALID_ERROR_CODE_MESSAGE, "Vendor Id")));

        VendorContact vendorContactDB = vendorContactDao.getVendorContactByVendorId(vendorDB.getId()).get();

        VendorPreference vendorPreferenceDB = vendorPreferenceDao.getVendorPreferenceByVendorId(vendorDB.getId()).get();

        Optional<VendorWhitelisting> vendorWhitelistingDB = vendorWhiteListingDao.getVendorWhiteListingByVendorId(vendorDB.getId());

        VendorBankDetails vendorBankDB = vendorBankDao.getVendorBankByVendorId(vendorDB.getId()).get();
        List<String> moduleNames = moduleDao.getActiveModuleNamesByVendorId(vendorId);
        log.info("creating response object for VendorDetailsResponse");
        VendorDetailsResponse vendorDetailsResponse = mapper.convertValue(vendorDB, VendorDetailsResponse.class);
        VendorPostResponse vendorPostResponse = VendorPostResponse.builder()
                .vendorDetails(vendorDetailsResponse)
                .contactDetails(mapper.convertValue(vendorContactDB, ContactDetailsRequest.class))
                        .preferenceDetails(mapper.convertValue(vendorPreferenceDB, PreferenceDetailsRequest.class))
                                .whitelistingDetails(ObjectUtils.isEmpty(vendorWhitelistingDB) ? null : mapper.convertValue(vendorWhitelistingDB, WhitelistingDetailsResponse.class))
                                        .bankDetailsRequest(mapper.convertValue(vendorBankDB, BankDetailsRequest.class))
                                            .modules(moduleNames)
                                                .build();

        return ResponseDto.<VendorPostResponse>builder().status(RESPONSE_SUCCESS).data(List.of(vendorPostResponse)).build();
    }

    public ResponseDto<VendorDetailsResponse> getAllVendors(VendorFilterRequest vendorFilterRequest, String search, Pageable pageable) {
        List<VendorDetailsResponse> allVendors = new ArrayList<>();
        Page<Vendor> vendors = vendorDetailsDao.getAllVendors(vendorFilterRequest, search, pageable);
        log.info(" Vendor data from page number:{}, page size:{}", pageable.getPageNumber(), pageable.getPageSize());
        vendors.stream().forEach(u -> {
            List<String> moduleNames = moduleDao.getActiveModuleNamesByVendorId(u.getId());
            VendorContact vendorContact = vendorContactDao.getVendorContactByVendorId(u.getId())
                    .orElseThrow(() -> new ValidationException(NOT_FOUND_ERROR_CODE, MessageFormat.format(NOT_FOUND_ERROR_MESSAGE, "Vendor contact")));
            VendorDetailsResponse vendorDetailsResponse = mapper.convertValue(u, VendorDetailsResponse.class);
            vendorDetailsResponse.setModules(moduleNames);
            vendorDetailsResponse.setContactFirstName(vendorContact.getFirstName());
            vendorDetailsResponse.setContactLastName(vendorContact.getLastName());
            vendorDetailsResponse.setContactNumber(vendorContact.getPhoneNumber());
            allVendors.add(vendorDetailsResponse);
        });
        return ResponseDto.<VendorDetailsResponse>builder().status(RESPONSE_SUCCESS)
                .data(allVendors).count((long) allVendors.size())
                .total(vendors.getTotalElements()).build();
    }

    public ResponseDto<VendorFilterListResponse> getVendorFilterData(String status) {
        List<Vendor> vendorList = vendorDetailsDao.getAllByStatus(status);
        List<VendorFilterListResponse> vendorFilterListResponses = vendorMapper.mapVendorToResponse(vendorList, VendorFilterListResponse.class);
        return ResponseDto.<VendorFilterListResponse>builder()
                .status(RESPONSE_SUCCESS)
                .data(vendorFilterListResponses)
                .build();
    }

    private void loadVendorCINToEndpointMap() {
        List<Object[]> vendorPreferenceDetails = vendorPreferenceDao.getAllVendorCINAndEndPoint();
        vendorCINToEndpoints = new HashMap<>();
        log.info("Mapping vendorCIN to endPoint.");

        if (!CollectionUtils.isEmpty(vendorPreferenceDetails)) {
            vendorCINToEndpoints = vendorPreferenceDetails.stream()
                    .filter(details -> details[0] != null && details[1] != null)
                    .collect(Collectors.toMap(
                            details -> (String) details[0],
                            details -> (String) details[1]
                    ));
        } else {
            log.info("No Data found to map with vendorCIN to endPoint");
        }
    }
    public String getVendorCINToEndpointMap(String vendorCIN) {
        return vendorCINToEndpoints.get(vendorCIN);
    }

    private boolean isVendorWhiteListingDetailsUpdated(VendorWhitelisting vendorWhitelistingDB, WhitelistingDetailsRequest whitelistingDetailsRequest) {

        vendorWhitelistingDB.setFontFamily(whitelistingDetailsRequest.getFontFamily());
        vendorWhitelistingDB.setPrimaryColor(whitelistingDetailsRequest.getPrimaryColor());
        vendorWhitelistingDB.setSecondaryColor(whitelistingDetailsRequest.getSecondaryColor());
        if (ObjectUtils.isNotEmpty(whitelistingDetailsRequest.getFontFile())) {
            vendorWhiteListingDao.getByFileId(whitelistingDetailsRequest.getFontFile())
                    .orElseThrow(() -> new ValidationException(NOT_FOUND_ERROR_CODE, MessageFormat.format(NOT_FOUND_ERROR_MESSAGE,"Font Id")));
            vendorWhitelistingDB.setFontFile(whitelistingDetailsRequest.getFontFile());
        } else {
            vendorWhitelistingDB.setFontFile(null);
        }
        vendorWhitelistingDB.setDomain(whitelistingDetailsRequest.getDomain());
        vendorWhitelistingDB.setVendorEndpoint(whitelistingDetailsRequest.getVendorEndpoint());
        vendorWhitelistingDB.setDatabaseOnPremise(whitelistingDetailsRequest.getDatabaseOnPremise());
        return true;

    }

    private boolean isVendorPreferenceUpdated(VendorPreference vendorPreferenceDB, PreferenceDetailsRequest preferenceDetailsRequest) {
        boolean isDataUpdated = false;
        if(ObjectUtils.isEmpty(preferenceDetailsRequest)){
            return isDataUpdated;
        }
        if (StringUtils.isNotEmpty(preferenceDetailsRequest.getProductId())) {
            vendorPreferenceDB.setProductId(preferenceDetailsRequest.getProductId());
            isDataUpdated = true;
        }
        if (Optional.of(preferenceDetailsRequest.getTotalMaxAdmin()).isPresent()) {
            vendorPreferenceDB.setTotalMaxAdmin(preferenceDetailsRequest.getTotalMaxAdmin());
            isDataUpdated = true;
        }
        if (StringUtils.isNotEmpty(preferenceDetailsRequest.getDataProviderProtocol())) {
            vendorPreferenceDB.setDataProviderProtocol(preferenceDetailsRequest.getDataProviderProtocol());
            isDataUpdated = true;
        }
        if(StringUtils.isNotEmpty(preferenceDetailsRequest.getDataAPIEndpoint())){
            vendorPreferenceDB.setDataAPIEndpoint(preferenceDetailsRequest.getDataAPIEndpoint());
            isDataUpdated = true;
        }
        return isDataUpdated;
    }

    private boolean isVendorBankDetailsUpdated(VendorBankDetails vendorBankDetails, BankDetailsRequest bankDetailsRequest) {
        boolean isDataUpdated = false;
        if(ObjectUtils.isEmpty(bankDetailsRequest)){
            return isDataUpdated;
        }
        if(StringUtils.isNotEmpty(bankDetailsRequest.getBankAccountName())) {
            vendorBankDetails.setBankAccountName(bankDetailsRequest.getBankAccountName());
            isDataUpdated = true;
        }
        if(StringUtils.isNotEmpty(bankDetailsRequest.getBankAccountType())) {
            vendorBankDetails.setBankAccountType(bankDetailsRequest.getBankAccountType());
            isDataUpdated = true;
        }
        if(StringUtils.isNotEmpty(bankDetailsRequest.getBankAccountNumber())) {
            vendorBankDetails.setBankAccountNumber(bankDetailsRequest.getBankAccountNumber());
            isDataUpdated = true;
        }
        if(StringUtils.isNotEmpty(bankDetailsRequest.getIfscCode())) {
            vendorBankDetails.setIfscCode(bankDetailsRequest.getIfscCode());
            isDataUpdated = true;
        }
        if(StringUtils.isNotEmpty(bankDetailsRequest.getCin())) {
            vendorBankDetails.setCin(bankDetailsRequest.getCin());
            isDataUpdated = true;
        }
        if(StringUtils.isNotEmpty(bankDetailsRequest.getGst())) {
            vendorBankDetails.setGst(bankDetailsRequest.getGst());
            isDataUpdated = true;
        }
        if(StringUtils.isNotEmpty(bankDetailsRequest.getPan())) {
            vendorBankDetails.setPan(bankDetailsRequest.getPan());
            isDataUpdated = true;
        }
        if(StringUtils.isNotEmpty(bankDetailsRequest.getTin())) {
            vendorBankDetails.setTin(bankDetailsRequest.getTin());
            isDataUpdated = true;
        }
        return isDataUpdated;
    }

    private boolean isVendorBasicDataUpdated(Vendor vendorDB, VendorUpdateRequest vendorRequest) {
        boolean isDataUpdated = false;

        if (StringUtils.isNotEmpty(vendorRequest.getName())) {
            vendorDB.setName(vendorRequest.getName());
            isDataUpdated = true;
        }
        if(StringUtils.isNotEmpty(vendorRequest.getStatus())){
            vendorDB.setStatus(vendorRequest.getStatus());
            isDataUpdated = true;
        }

        if (ObjectUtils.isNotEmpty(vendorRequest.getAddress())) {
            if (StringUtils.isNotEmpty(vendorRequest.getAddress().getAddress1())) {
                vendorDB.setAddress1(vendorRequest.getAddress().getAddress1());
                isDataUpdated = true;
            }
            if (StringUtils.isNotEmpty(vendorRequest.getAddress().getAddress2())) {
                vendorDB.setAddress2(vendorRequest.getAddress().getAddress2());
                isDataUpdated = true;
            }
            if (StringUtils.isNotEmpty(vendorRequest.getAddress().getCity())) {
                vendorDB.setCity(vendorRequest.getAddress().getCity());
                isDataUpdated = true;
            }
            if (StringUtils.isNotEmpty(vendorRequest.getAddress().getState())) {
                vendorDB.setState(vendorRequest.getAddress().getState());
                isDataUpdated = true;
            }
            if (StringUtils.isNotEmpty(vendorRequest.getAddress().getCountry())) {
                vendorDB.setCountry(vendorRequest.getAddress().getCountry());
                isDataUpdated = true;
            }
            if (StringUtils.isNotEmpty(vendorRequest.getAddress().getPincode())) {
                vendorDB.setPincode(vendorRequest.getAddress().getPincode());
                isDataUpdated = true;
            }
        }
        return isDataUpdated;
    }

    private List<VendorModules> buildModule(List<ModuleRequest> ModuleRequestList) {
        List<VendorModules> modules = new ArrayList<>();
        for (ModuleRequest module : ModuleRequestList) {
            Module moduleResponse = moduleDao.getByModuleId(module.getId())
                    .orElseThrow(() -> new CMSException(ErrorConstants.NOT_FOUND_ERROR_CODE, MessageFormat.format(ErrorConstants.NOT_FOUND_ERROR_MESSAGE, "Module")));
            VendorModules vendorModules = VendorModules.builder()
                    .module(moduleResponse)
                    .active(module.isActive())
                    .build();
            modules.add(vendorModules);
        }
        return modules;
    }

    private boolean isVendorContactDetailsUpdated(VendorContact vendorContactDB, ContactDetailsRequest contactDetailsRequest) {

        boolean isDataUpdated = false;
        if(!ObjectUtils.isNotEmpty(contactDetailsRequest)){
            return isDataUpdated;
        }
        if (StringUtils.isNotEmpty(contactDetailsRequest.getFirstName())) {
            vendorContactDB.setFirstName(contactDetailsRequest.getFirstName());
            isDataUpdated = true;
        }
        if (StringUtils.isNotEmpty(contactDetailsRequest.getLastName())) {
            vendorContactDB.setLastName(contactDetailsRequest.getLastName());
            isDataUpdated = true;
        }
        if (StringUtils.isNotEmpty(contactDetailsRequest.getEmail())) {
            throw new CMSException(ErrorConstants.CANNOT_UPDATE_FIELD_CODE, MessageFormat.format(ErrorConstants.CANNOT_UPDATE_FIELD_MESSAGE, "Email"));
        }
        if (StringUtils.isNotEmpty(contactDetailsRequest.getPhoneNumber())) {
            vendorContactDB.setPhoneNumber(contactDetailsRequest.getPhoneNumber());
            isDataUpdated = true;
        }
        return isDataUpdated;
    }

    private List<VendorModules> buildVendorModule(Vendor vendor, List<ModuleRequest> moduleList) {
        List<VendorModules> updatedData = new ArrayList<>();
        List<VendorModules> dbVendorModules = moduleDao.getAllModulesByVendorId(vendor.getId());
        Map<UUID, ModuleRequest> moduleRequestMap = moduleList.stream()
                .collect(Collectors.toMap(ModuleRequest::getId, module -> module));

        for (VendorModules vendorModule : dbVendorModules) {
            UUID moduleId = vendorModule.getModule().getId();
            if (moduleRequestMap.containsKey(moduleId)) {
                ModuleRequest moduleRequest = moduleRequestMap.get(moduleId);
                vendorModule.setActive(moduleRequest.isActive());
            }
        }
        return updatedData;
    }
}
