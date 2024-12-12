package com.continuum.cms.service;

import com.continuum.cms.dao.MasterDao;
import com.continuum.cms.entity.Master;
import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.model.response.MasterPostResponse;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class MasterServiceTest {

    @Mock
    private MasterDao masterDao;

    @InjectMocks
    private MasterService masterService;

    private final Master country = CountryBuilder.buildCountryRequest("IND", "India");
    private final MasterPostResponse masterPostResponse = MasterResponseBuilder.buildMasterRequest("IND","India");

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMasterDataWithValidCountryCodeReturnsListOfStates() {

        List<MasterPostResponse> expectedStateList = Collections.singletonList(masterPostResponse);
        when(masterDao.getAllStateByCountryCode(country.getCountryCode())).thenReturn(expectedStateList);

        ResponseDto<MasterPostResponse> response = masterService.getCountryStateData(country.getCountryCode());

        assertNotNull(response);
        assertEquals(AppConstants.RESPONSE_SUCCESS, response.getStatus());
        assertEquals(expectedStateList, response.getData());
        verify(masterDao, times(1)).getAllStateByCountryCode(country.getCountryCode());
        verify(masterDao, times(0)).getALlCountryNames();
    }

    @Test
    public void testMasterDataWithInvalidCountryCodeThrowsCMSException() {
        String countryCode = "INVALID_CODE";
        when(masterDao.getAllStateByCountryCode(countryCode)).thenReturn(Collections.emptyList());

        assertThrows(CMSException.class, () -> masterService.getCountryStateData(countryCode));
        verify(masterDao, times(1)).getAllStateByCountryCode(countryCode);
        verify(masterDao, times(0)).getALlCountryNames();
    }

    @Test
    public void testGetAllCountries_WithEmptyCountryCode_ReturnsListOfCountries() {
        List<MasterPostResponse> expectedCountryList = Collections.singletonList(masterPostResponse);
        when(masterDao.getALlCountryNames()).thenReturn(expectedCountryList);

        ResponseDto<MasterPostResponse> response = masterService.getCountryStateData(null);

        assertNotNull(response);
        assertEquals(AppConstants.RESPONSE_SUCCESS, response.getStatus());
        assertEquals(expectedCountryList, response.getData());
        verify(masterDao, times(0)).getAllStateByCountryCode(country.getCountryCode());
        verify(masterDao, times(1)).getALlCountryNames();

    }

    @Test
    public void testGetDropDownWithStatus() {
        List<String> statusList = Arrays.asList("Active", "InActive", "Blocked", "Expired", "Completed", "Pending", "PasswordExpired");

        ResponseDto<String> response = masterService.getMasterData("STATUS");

        assertEquals(AppConstants.RESPONSE_SUCCESS, response.getStatus());
        assertEquals(statusList, response.getData());
    }

    @Test
    public void testGetDropDownWithAccount() {
        List<String> statusList = Arrays.asList("Savings", "Current");

        ResponseDto<String> response = masterService.getMasterData("ACCOUNT");

        assertEquals(AppConstants.RESPONSE_SUCCESS, response.getStatus());
        assertEquals(statusList, response.getData());
    }

    @Test
    public void testGetDropDownWithDataProvider() {
        List<String> statusList = Arrays.asList("OCPP", "OCPI");

        ResponseDto<String> response = masterService.getMasterData("DATAPROVIDER");

        assertEquals(AppConstants.RESPONSE_SUCCESS, response.getStatus());
        assertEquals(statusList, response.getData());
    }

    @Test
    public void testGetDropDownWithDatabase() {
        List<String> statusList = Arrays.asList("On-prem", "Cloud");

        ResponseDto<String> response = masterService.getMasterData("DATABASE");

        assertEquals(AppConstants.RESPONSE_SUCCESS, response.getStatus());
        assertEquals(statusList, response.getData());
    }

    @Test
    public void testGetDropDownWithInvalidValue() {
        List<String> statusList = Arrays.asList();

        ResponseDto<String> response = masterService.getMasterData("INVALID");

        assertEquals(AppConstants.RESPONSE_FAILURE, response.getStatus());
        assertEquals(Collections.singletonList("No value present"), response.getData());
    }
}