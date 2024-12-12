package com.continuum.cms.service;

import com.continuum.cms.dao.MasterDao;
import com.continuum.cms.entity.Module;
import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.model.response.MasterPostResponse;
import com.continuum.cms.model.response.ModulesPostResponse;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.util.AppConstants;
import com.continuum.cms.util.ErrorConstants;
import com.continuum.cms.util.enums.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class MasterService {

    private final MasterDao masterDao;

    public ResponseDto<MasterPostResponse> getCountryStateData(String countryCode) {
        log.info("Getting all country state code and name");
        if(StringUtils.isNotEmpty(countryCode)) {
            List<MasterPostResponse> stateList = masterDao.getAllStateByCountryCode(countryCode);
            if(ObjectUtils.isEmpty(stateList)) {
                throw new CMSException(ErrorConstants.INVALID_ERROR_CODE, MessageFormat.format(ErrorConstants.INVALID_ERROR_CODE_MESSAGE, "Country Code"));
            }
            return ResponseDto.<MasterPostResponse>builder()
                    .status(AppConstants.RESPONSE_SUCCESS)
                    .data(stateList)
                    .build();
        } else {
            List<MasterPostResponse> countryList = masterDao.getALlCountryNames();
            return ResponseDto.<MasterPostResponse>builder()
                    .status(AppConstants.RESPONSE_SUCCESS)
                    .data(countryList)
                    .build();
        }

    }


    public ResponseDto<String> getMasterData(String masterDataType) {
        log.info("Getting masterDataType name for {}", masterDataType);
        List<String> masterDataList;
        try {
            switch (MasterDataType.valueOf(masterDataType.toUpperCase())) {
                case STATUS:
                    masterDataList = Stream.of(Status.values())
                            .map(Status::getName)
                            .collect(Collectors.toList());
                    break;
                case ACCOUNT:
                    masterDataList = Stream.of(Account.values())
                            .map(Account::getName)
                            .collect(Collectors.toList());
                    break;
                case DATAPROVIDER:
                    masterDataList = Stream.of(DataProvider.values())
                            .map(DataProvider::getName)
                            .collect(Collectors.toList());
                    break;
                case DATABASE:
                    masterDataList = Stream.of(Database.values())
                            .map(Database::getName)
                            .collect(Collectors.toList());
                    break;
                default:
                    return ResponseDto.<String>builder()
                            .status(AppConstants.RESPONSE_FAILURE)
                            .data(List.of("No value present"))
                            .build();
            }
            return ResponseDto.<String>builder()
                    .status(AppConstants.RESPONSE_SUCCESS)
                    .data(masterDataList)
                    .build();
        } catch (Exception ex) {
            log.error("There is some error in fetching master data {}", ex.getMessage());
            return ResponseDto.<String>builder()
                    .status(AppConstants.RESPONSE_FAILURE)
                    .data(List.of("No value present"))
                    .build();
        }

    }

    public ResponseDto<ModulesPostResponse> getAllModules() {
        List<Module> moduleList = masterDao.getAllModules();
        List<ModulesPostResponse> modulesPostResponses = mapModulesList(moduleList);
        log.info("Got module list");
        return ResponseDto.<ModulesPostResponse>builder()
                .status(AppConstants.RESPONSE_SUCCESS)
                .data(modulesPostResponses)
                .build();
    }

    private List<ModulesPostResponse> mapModulesList(List<Module> modules) {
        return modules.stream()
                .map(module -> ModulesPostResponse.builder()
                        .id(module.getId())
                        .moduleName(module.getModuleName())
                        .moduleDescription(module.getModuleDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
