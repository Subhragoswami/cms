package com.continuum.cms.service;

import com.continuum.cms.auth.security.JwtService;
import com.continuum.cms.dao.ChargingStationDao;

import com.continuum.cms.entity.ChargerDetail;
import com.continuum.cms.entity.ChargingStation;
import com.continuum.cms.entity.ConnectorDetail;
import com.continuum.cms.entity.EVSEDetail;
import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.exceptions.ValidationException;
import com.continuum.cms.mapper.ChargingStationMapper;
import com.continuum.cms.model.external.requests.ChargingStationFilter;
import com.continuum.cms.model.response.ChargingStationDetailsResponse;
import com.continuum.cms.model.response.ChargingStationResponse;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.service.external.VendorClientService;
import com.continuum.cms.util.AppConstants;
import com.continuum.cms.util.ErrorConstants;
import com.continuum.cms.util.MiscUtil;
import com.continuum.cms.util.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChargingStationService {
    private final VendorClientService vendorClientService;
    private final MiscUtil miscUtil;
    private final ChargingStationDao chargingStationDao;
    private final ChargingStationMapper chargingStationMapper;
    private final JwtService jwtService;

    public Object getChargingStationDetails(String token, Pageable pageable, String search, ChargingStationFilter chargingStationFilter, String userName) {
        String roles = jwtService.extractRoles(token);
        if(roles.equals(Role.ROLE_SUPER_ADMIN.getName())) {
            return getChargingStationDetailsForSuperAdmin(chargingStationFilter , search, pageable);
        } else if (roles.equals(Role.ROLE_VENDOR_ADMIN.getName())) {
            return getChargingStationDetailsForVendorAdmin(userName, pageable, search, chargingStationFilter);
        }
        throw new CMSException(ErrorConstants.NOT_FOUND_ERROR_CODE, MessageFormat.format(ErrorConstants.NOT_FOUND_ERROR_MESSAGE,"Roles"));
    }

    public Object getChargingStationDetailsForVendorAdmin(String userName, Pageable pageable, String search, ChargingStationFilter chargingStationFilter) {
        log.info("getting Charging Station list for Vendor admin");
        String vendorEndpoint =  miscUtil.validateUserNameAndGetVendorEndPoint(userName);
        return vendorClientService.getChargingStationData(vendorEndpoint, pageable, search, chargingStationFilter);
    }

    public ResponseDto<ChargingStationResponse> getChargingStationDetailsForSuperAdmin(ChargingStationFilter chargingStationFilter, String search, Pageable pageable) {
        log.info("getting Charging Station list for Super admin");
        List<String> cinList = chargingStationDao.getCinByVendorIds(chargingStationFilter.getVendorId());
        Page<ChargingStation> chargingStations = chargingStationDao.getAllChargingStation(chargingStationFilter.getCities(), cinList, search, pageable);
        List<ChargingStationResponse> chargingStationDetailsResponses = chargingStationMapper.mapChargingStationListToResponse(chargingStations, ChargingStationResponse.class);
        chargingStationDetailsResponses.stream().forEach(c -> {
            chargingStationDao.getVendorByCin(c.getVendorCode()).ifPresent(result -> {
                c.setVendorId(result.getId());
                c.setVendorName(result.getName());
            });
        });
        return ResponseDto.<ChargingStationResponse>builder()
                .status(AppConstants.RESPONSE_SUCCESS)
                .data(chargingStationDetailsResponses)
                .count(chargingStations.stream().count())
                .total(chargingStations.getTotalElements())
                .build();
    }

    public Object getChargingStationDetailsByLocationId(String token, long id, String userName) {
        String roles = jwtService.extractRoles(token);
        if(roles.equals(Role.ROLE_SUPER_ADMIN.getName())) {
            return getChargingStationForSuperAdminByLocationId(id);
        } else if (roles.equals(Role.ROLE_VENDOR_ADMIN.getName())) {
            return getChargingStationForVendorAdminByLocationId(userName, id);
        }
        throw new CMSException(ErrorConstants.NOT_FOUND_ERROR_CODE, MessageFormat.format(ErrorConstants.NOT_FOUND_ERROR_MESSAGE,"Roles"));
    }

    public Object getCityList(String token, String userName) {
        String roles = jwtService.extractRoles(token);
        if(roles.equals(Role.ROLE_SUPER_ADMIN.getName())) {
            return getCityListForSuperAdmin();
        } else if (roles.equals(Role.ROLE_VENDOR_ADMIN.getName())) {
            return getCityListForVendorAdmin(userName);
        }
        throw new CMSException(ErrorConstants.NOT_FOUND_ERROR_CODE, MessageFormat.format(ErrorConstants.NOT_FOUND_ERROR_MESSAGE,"Roles"));
    }

    public Object getCityListForVendorAdmin(String userName) {
        log.info("getting cityList for Vendor admin");
        String vendorEndpoint =  miscUtil.validateUserNameAndGetVendorEndPoint(userName);
        return vendorClientService.getVendorCityList(vendorEndpoint);
    }

    public ResponseDto<String> getCityListForSuperAdmin(){
        log.info("getting cityList for Super admin");
        List<String> chargingStations = chargingStationDao.getCityList();
        return ResponseDto.<String>builder()
                .status(AppConstants.RESPONSE_SUCCESS)
                .data((chargingStations))
                .build();
    }

    public ResponseDto<ChargingStationDetailsResponse> getChargingStationForSuperAdminByLocationId(long locationId) {
        log.info("getting Charging Station for Super admin by location id {}",locationId);
        ChargingStation chargingStationDetails = chargingStationDao.findByLocationId(locationId).orElseThrow(
                () -> new ValidationException(ErrorConstants.NOT_FOUND_ERROR_CODE, MessageFormat.format(ErrorConstants.NOT_FOUND_ERROR_MESSAGE, "location id")));
        ChargingStationDetailsResponse chargingStationUserResponse = setChargingStationUserResponse(chargingStationDetails);
        return ResponseDto.<ChargingStationDetailsResponse>builder()
                .status(AppConstants.RESPONSE_SUCCESS)
                .data(List.of(chargingStationUserResponse))
                .build();
    }

    public Object getChargingStationForVendorAdminByLocationId(String userName, long id) {
        log.info("getting Charging Station for Vendor admin by location id {}",id);
        String vendorEndpoint =  miscUtil.validateUserNameAndGetVendorEndPoint(userName);
        return vendorClientService.getStationDetailsById(id, vendorEndpoint);
    }

    private ChargingStationDetailsResponse setChargingStationUserResponse(ChargingStation chargingStationDetails){
        log.info("Making ChargingStationDetails Response Object from charging station ");
        List<EVSEDetail> evseDetailsList = new ArrayList<>();
        List<ConnectorDetail> connectorDetailsList= new ArrayList<>();
        BigDecimal carbonCredits = chargingStationDao.getTotalCC(chargingStationDetails.getLocationId());
        List<ChargerDetail> chargerDetailsList = chargingStationDao.getByLocationId(chargingStationDetails.getLocationId());
        for(ChargerDetail chargerDetails : chargerDetailsList) {
            List<EVSEDetail> evseDetails = chargingStationDao.getByChargerId(chargerDetails.getChargerId());
            for(EVSEDetail evseDetail : evseDetails) {
                List<ConnectorDetail> connectorDetails = chargingStationDao.getByEvseId(evseDetail.getEvseId());
                connectorDetailsList.addAll(connectorDetails);
            }
            evseDetailsList.addAll(evseDetails);
        }
        ChargingStationDetailsResponse chargingStationResponse = chargingStationMapper.mapToChargingStationToResponse(chargingStationDetails, chargerDetailsList, evseDetailsList, connectorDetailsList);
        chargingStationResponse.setCarbonCreditGenerated(carbonCredits);
        return chargingStationResponse;
    }
}
