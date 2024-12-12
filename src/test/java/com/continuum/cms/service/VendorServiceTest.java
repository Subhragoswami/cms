package com.continuum.cms.service;

import com.continuum.cms.dao.ModuleDao;
import com.continuum.cms.entity.Module;
import com.continuum.cms.entity.Vendor;
import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.model.request.VendorModulePostRequest;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class VendorServiceTest {

    @Mock
    private ModuleDao moduleDao;

    @InjectMocks
    VendorService vendorService;

    private final Vendor vendor =  VendorBuilder.buildDefaultVendor("mockName","mockCode", "mockStatus");
    private final Module module = ModuleBuilder.buildDefaultModule("moduleName","moduleDescription");

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void saveVendorModuleWithValidVendorId() {
        VendorModulePostRequest vendorModulePostRequest = VendorModulePostRequest.builder()
                .vendorId(UUID.randomUUID())
                .modules(List.of(UUID.fromString("48a1403c-c085-43b7-9958-7752993bafe2")))
                .build();
        when(moduleDao.getVendorById(any())).thenReturn(Optional.of(vendor));
        when(moduleDao.getByModuleId(any())).thenReturn(Optional.of(module));

        ResponseDto<String> responseDto = vendorService.saveVendorModule(vendorModulePostRequest);

        assertEquals(AppConstants.RESPONSE_SUCCESS, responseDto.getStatus());
        assertEquals(List.of("Vendor modules configured successfully"), responseDto.getData());

        verify(moduleDao, times(1)).getVendorById(any());
        verify(moduleDao, times(1)).saveVendorModule(ArgumentMatchers.anyList());
    }

    @Test
    void saveVendorModuleWithInValidVendorId() {
        VendorModulePostRequest vendorModulePostRequest = VendorModulePostRequest.builder()
                .vendorId(UUID.randomUUID())
                .modules(List.of(UUID.fromString("48a1403c-c085-43b7-9958-7752993bafe2")))
                .build();
        when(moduleDao.getVendorById(any())).thenReturn(Optional.empty());
        when(moduleDao.getByModuleId(any())).thenReturn(Optional.of(module));

        CMSException exception = assertThrows(CMSException.class, () -> vendorService.saveVendorModule(vendorModulePostRequest));

        assertEquals(ErrorConstants.INVALID_ERROR_CODE, exception.getErrorCode());
        assertEquals(MessageFormat.format(ErrorConstants.INVALID_ERROR_CODE_MESSAGE, "Vendor ID"), exception.getErrorMessage());

        verify(moduleDao, times(1)).getVendorById(any());
        verify(moduleDao, times(0)).saveVendorModule(ArgumentMatchers.anyList());
    }

}