package com.continuum.cms.service;

import com.continuum.cms.dao.*;
import com.continuum.cms.entity.*;
import com.continuum.cms.entity.Module;
import com.continuum.cms.exceptions.ValidationException;
import com.continuum.cms.model.request.*;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.model.response.VendorDetailsResponse;
import com.continuum.cms.model.response.VendorPostResponse;
import com.continuum.cms.model.response.WhitelistingDetailsResponse;
import com.continuum.cms.util.AppConstants;
import com.continuum.cms.util.ErrorConstants;
import com.continuum.cms.util.VendorDetailsRequest;
import com.continuum.cms.validator.UserValidator;
import com.continuum.cms.validator.VendorDetailsValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendorDetailsServiceTest {

    @Mock
    private UserValidator userValidator;

    @Mock
    private UserDao userDao;

    @Mock
    private ModuleDao moduleDao;

    @Mock
    private VendorDetailsDao vendorDetailsDao;

    @Mock
    private VendorBankDao vendorBankDao;

    @Mock
    private VendorPreferenceDao vendorPreferenceDao;

    @Mock
    private VendorWhiteListingDao vendorWhiteListingDao;

    @Mock
    private VendorContactDao vendorContactDao;

    @Mock
    private VendorDetailsValidator vendorDetailsValidator;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private VendorDetailsService vendorService;

    private final VendorRequest vendorRequest = VendorDetailsRequest.createVendorRequest();

    @BeforeEach
    void setUp() {
    }

    @Test
    void addVendor() {

        UserDetailsRequest userDetails = new UserDetailsRequest();
        userDetails.setEmail("example@example.com");
        vendorRequest.setUserDetails(userDetails);
        MultipartFile fontFile = new MockMultipartFile("fontFile", new byte[]{});
        MultipartFile logoFile = new MockMultipartFile("logoFile", new byte[]{});

        when(userDao.getByEmail(anyString())).thenReturn(Optional.empty());
        when(userDao.findRoleByName(anyString())).thenReturn(Optional.of(new Roles()));
        when(moduleDao.getByModuleId(any()))
                .thenAnswer(invocation -> {
                    Module module = new Module();
                    module.setModuleName("module");
                    return Optional.of(module);
                });

        when(vendorDetailsDao.getVendorByCin(anyString())).thenReturn(Optional.empty());

        ResponseDto<String> response = vendorService.addVendor(vendorRequest);

        assertEquals(AppConstants.RESPONSE_SUCCESS, response.getStatus());
        assertEquals("vendor added successfully", response.getData().get(0));

        verify(userDao, times(1)).getByEmail(anyString());
        verify(vendorDetailsDao, times(1)).getVendorByCin(anyString());
        verify(vendorDetailsValidator, times(1)).requestValidation(eq(vendorRequest));
        verify(vendorDetailsDao, times(1)).saveDetails(eq(vendorRequest), any(), any(UserRoles.class));
    }

    @Test
    void addVendorUserEmailAlreadyExistsThrowsValidationException() {
        UserDetailsRequest userDetails = new UserDetailsRequest();
        userDetails.setEmail("example@example.com"); // Set email or other necessary properties
        vendorRequest.setUserDetails(userDetails);
        MultipartFile fontFile = new MockMultipartFile("fontFile", new byte[]{});
        MultipartFile logoFile = new MockMultipartFile("logoFile", new byte[]{});

        when(userDao.getByEmail(anyString())).thenReturn(Optional.of(new User()));


        assertThrows(ValidationException.class,
                () -> vendorService.addVendor(vendorRequest));
        verify(userDao, times(1)).getByEmail(eq("example@example.com"));
    }


    @Test
    void getVendorDetailByIdSuccess() {

        UUID vendorId = UUID.randomUUID();

        Vendor vendorDB = new Vendor();

        VendorContact vendorContactDB = new VendorContact();

        VendorPreference vendorPreferenceDB = new VendorPreference();

        VendorWhitelisting vendorWhitelistingDB = new VendorWhitelisting();

        VendorBankDetails vendorBankDB = new VendorBankDetails();
        VendorDetailsResponse vendorDetailsResponse = new VendorDetailsResponse();

        List<String> moduleNames = List.of("Module1", "Module2");

        when(vendorDetailsDao.getVendorById(any())).thenReturn(Optional.of(vendorDB));
        when(vendorContactDao.getVendorContactByVendorId(any())).thenReturn(Optional.of(vendorContactDB));
        when(vendorPreferenceDao.getVendorPreferenceByVendorId(any())).thenReturn(Optional.of(vendorPreferenceDB));
        when(vendorWhiteListingDao.getVendorWhiteListingByVendorId(any())).thenReturn(Optional.of(vendorWhitelistingDB));
        when(vendorBankDao.getVendorBankByVendorId(any())).thenReturn(Optional.of(vendorBankDB));
        when(moduleDao.getActiveModuleNamesByVendorId(any())).thenReturn(List.of("Module1", "Module2"));
        when(objectMapper.convertValue(any(), eq(VendorDetailsResponse.class))).thenReturn(vendorDetailsResponse);
        when(objectMapper.convertValue(any(), eq(ContactDetailsRequest.class))).thenReturn(new ContactDetailsRequest());
        when(objectMapper.convertValue(any(), eq(PreferenceDetailsRequest.class))).thenReturn(new PreferenceDetailsRequest());
        when(objectMapper.convertValue(any(), eq(WhitelistingDetailsResponse.class))).thenReturn(new WhitelistingDetailsResponse());
        when(objectMapper.convertValue(any(), eq(BankDetailsRequest.class))).thenReturn(new BankDetailsRequest());

        ResponseDto<VendorPostResponse> response = vendorService.getVendorDetailById(vendorId);

        assertEquals(AppConstants.RESPONSE_SUCCESS, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());


    }

    @Test
    void getVendorDetailByIdVendorNotFound() {
        UUID vendorId = UUID.randomUUID();
        when(vendorDetailsDao.getVendorById(vendorId)).thenReturn(Optional.empty());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            vendorService.getVendorDetailById(vendorId);
        });

        assertEquals(ErrorConstants.INVALID_ERROR_CODE, exception.getErrorCode());

        verify(vendorDetailsDao, times(1)).getVendorById(eq(vendorId));
        verify(vendorContactDao, times(0)).getVendorContactByVendorId(eq(vendorId));
        verify(vendorPreferenceDao, times(0)).getVendorPreferenceByVendorId(eq(vendorId));
        verify(vendorWhiteListingDao, times(0)).getVendorWhiteListingByVendorId(eq(vendorId));
        verify(vendorBankDao, times(0)).getVendorBankByVendorId(eq(vendorId));
        verify(moduleDao, times(0)).getAllModulesByVendorId(eq(vendorId));
    }
}