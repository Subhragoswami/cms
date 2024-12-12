package com.continuum.cms.service;

import com.continuum.cms.dao.ModuleDao;
import com.continuum.cms.entity.Module;
import com.continuum.cms.entity.Vendor;
import com.continuum.cms.entity.VendorModules;
import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.model.request.VendorModulePostRequest;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.service.external.VendorClientService;
import com.continuum.cms.util.AppConstants;
import com.continuum.cms.util.ErrorConstants;
import com.continuum.cms.util.MiscUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VendorService {

    private final ModuleDao moduleDao;
    private final VendorClientService vendorClientService;
    private final MiscUtil miscUtil;


    public ResponseDto<String> saveVendorModule(VendorModulePostRequest vendorModulePostRequest) {
        Vendor vendor = moduleDao.getVendorById(vendorModulePostRequest.getVendorId())
                .orElseThrow(() -> new CMSException(ErrorConstants.INVALID_ERROR_CODE, MessageFormat.format(ErrorConstants.INVALID_ERROR_CODE_MESSAGE, "Vendor ID")));
        List<VendorModules> vendorModules = buildVendorModule(vendor, vendorModulePostRequest.getModules());
        moduleDao.saveVendorModule(vendorModules);
        return ResponseDto.<String>builder()
                .status(AppConstants.RESPONSE_SUCCESS)
                .data(List.of("Vendor modules configured successfully"))
                .build();
    }

    public Object getVendorDashboardDetails(String userName) {
        log.info("Fetching vendor endpoint for userName: {}", userName);
        String vendorEndpoint = miscUtil.validateUserNameAndGetVendorEndPoint(userName);
        log.info("Fetching vendor dashboard data from endpoint: {}", vendorEndpoint);
        Object dashboardData = vendorClientService.getVendorDashboardData(vendorEndpoint);
        Map<String, Object> dashboardMap = (Map<String, Object>) dashboardData;
        setDashboardData(dashboardMap);
        log.info("Returning dashboard data for userName: {}", userName);
        return dashboardData;
    }
    private void setDashboardData(Map dashboardMap){
        if (dashboardMap.containsKey("data")) {
            Object dataObject = dashboardMap.get("data");
            if (dataObject instanceof List) {
                List<Object> dataList = (List<Object>) dataObject;
                if (!dataList.isEmpty()) {
                    Object firstItem = dataList.get(0);
                    if (firstItem instanceof Map) {
                        Map<String, Object> dataMap = (Map<String, Object>) firstItem;
                        if (dataMap.containsKey("noOfAdmins")) {
                            dataMap.put("noOfAdmins", moduleDao.getCount());
                        }
                    }
                }
            }
        }
    }

    private List<VendorModules> buildVendorModule(Vendor vendor, List<UUID> moduleList) {
        List<VendorModules> vendorModulesList = new ArrayList<>();
        for (UUID id : moduleList) {
            Module moduleResponse = moduleDao.getByModuleId(id)
                    .orElseThrow(() -> new CMSException(ErrorConstants.NOT_FOUND_ERROR_CODE, MessageFormat.format(ErrorConstants.NOT_FOUND_ERROR_MESSAGE, "Module")));
            VendorModules vendorModules = VendorModules.builder()
                    .vendor(vendor)
                    .module(moduleResponse)
                    .build();
            vendorModulesList.add(vendorModules);
        }
        return vendorModulesList;
    }
}
