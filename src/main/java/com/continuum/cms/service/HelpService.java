package com.continuum.cms.service;

import com.continuum.cms.config.CMSServiceConfig;
import com.continuum.cms.dao.UserDao;
import com.continuum.cms.dao.VendorDetailsDao;
import com.continuum.cms.entity.User;
import com.continuum.cms.entity.Vendor;
import com.continuum.cms.exceptions.ValidationException;
import com.continuum.cms.model.request.HelpRequest;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.util.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

import static com.continuum.cms.util.AppConstants.RESPONSE_SUCCESS;
import static com.continuum.cms.util.ErrorConstants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class HelpService {

    private final EmailService emailService;
    private final VendorDetailsDao vendorDetailsDao;
    private final CMSServiceConfig cmsServiceConfig;
    private final UserDao userDao;

    public ResponseDto<String> helpRecordCapture(HelpRequest helpRequest) {
        User user = userDao.getByUserName(helpRequest.getUserName()).orElseThrow(() -> new ValidationException(INVALID_ERROR_CODE,
                MessageFormat.format(INVALID_ERROR_CODE_MESSAGE, "UserName")));
        Vendor vendor = vendorDetailsDao.findVendorByUserId(helpRequest.getUserId()).orElseThrow(() -> new ValidationException(NOT_FOUND_ERROR_CODE,
                MessageFormat.format(NOT_FOUND_ERROR_MESSAGE, "vendor")));
        emailService.helpScreenEmail(cmsServiceConfig.getCmsHelpEmail(),user, vendor, helpRequest.getDescriptionOfHelp());
        return ResponseDto.<String>builder().status(RESPONSE_SUCCESS).data(List.of(AppConstants.DETAILS_CAPTURED_SUCCESSFULLY)).build();
    }
}
