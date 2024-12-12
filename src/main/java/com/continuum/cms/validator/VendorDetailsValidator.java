package com.continuum.cms.validator;

import com.continuum.cms.dao.ModuleDao;
import com.continuum.cms.dao.VendorWhiteListingDao;
import com.continuum.cms.entity.VendorBankDetails;
import com.continuum.cms.exceptions.ValidationException;
import com.continuum.cms.model.request.BankDetailsRequest;
import com.continuum.cms.model.request.ContactDetailsRequest;
import com.continuum.cms.model.request.PreferenceDetailsRequest;
import com.continuum.cms.model.request.VendorRequest;
import com.continuum.cms.util.ValidationUtil;
import com.continuum.cms.util.enums.Status;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

import java.util.Optional;

import static com.continuum.cms.util.ErrorConstants.*;

@Component
@RequiredArgsConstructor
public class VendorDetailsValidator {
    private final ValidationUtil validationUtil;
    private final VendorWhiteListingDao vendorWhiteListingDao;
    private final ModuleDao moduleDao;

    public void requestValidation(VendorRequest vendorRequest) {

        validationUtil.validateNotEmpty(vendorRequest.getContactDetails().getEmail(), "Email");
        validationUtil.validateEmail(vendorRequest.getContactDetails().getEmail());

        validationUtil.validateNotEmpty(vendorRequest.getContactDetails().getPhoneNumber(), "Phone number");
        validationUtil.validatePhoneNumber(vendorRequest.getContactDetails().getPhoneNumber());

        validateMaxVendorAdmins(vendorRequest.getPreferenceDetails().getTotalMaxAdmin());

        validationUtil.validateNotEmpty(vendorRequest.getName(), "Vendor Name");
        validationUtil.validateNotEmpty(vendorRequest.getAddress(), "Vendor Address");
        validationUtil.validateNotEmpty(vendorRequest.getContactDetails(), "Contact Details");
        validationUtil.validateNotEmpty(vendorRequest.getPreferenceDetails(), "Preference Details");
        validationUtil.validateNotEmpty(vendorRequest.getAddress().getAddress1(), "Address Line 1");
        validationUtil.validateNotEmpty(vendorRequest.getAddress().getAddress2(), "Address Line 2");
        validationUtil.validateNotEmpty(vendorRequest.getAddress().getState(), "State");
        validationUtil.validateNotEmpty(vendorRequest.getAddress().getCountry(), "Country");
        validationUtil.validateNotEmpty(vendorRequest.getAddress().getPincode(), "Pincode");
        validationUtil.validateNotEmpty(vendorRequest.getContactDetails().getFirstName(), "First Name");
        validationUtil.validateNotEmpty(vendorRequest.getContactDetails().getLastName(), "Last Name");
        validationUtil.validateNotEmpty(vendorRequest.getBankDetailsRequest().getCin(), "CIN");
        validationUtil.validateNotEmpty(vendorRequest.getBankDetailsRequest().getGst(), "GST");
        validationUtil.validateNotEmpty(vendorRequest.getBankDetailsRequest().getPan(), "PAN");
        validationUtil.validateNotEmpty(vendorRequest.getBankDetailsRequest().getTin(), "TIN");
        validationUtil.validateNotEmpty(vendorRequest.getBankDetailsRequest().getBankAccountName(), "Bank Account Name");
        validationUtil.validateNotEmpty(vendorRequest.getBankDetailsRequest().getBankAccountType(), "Bank Account Type");
        validationUtil.validateNotEmpty(vendorRequest.getBankDetailsRequest().getBankAccountNumber(), "Bank Account Number");
        validationUtil.validateNotEmpty(vendorRequest.getBankDetailsRequest().getIfscCode(), "IfSC Code");
        if(ObjectUtils.isNotEmpty(vendorRequest.getWhitelistingDetails())) {
            validationUtil.validateNotEmpty(vendorRequest.getWhitelistingDetails().getFontFile(), "Font File");
            vendorWhiteListingDao.getByFileId(vendorRequest.getWhitelistingDetails().getFontFile())
                    .orElseThrow(() -> new ValidationException(NOT_FOUND_ERROR_CODE, MessageFormat.format(NOT_FOUND_ERROR_MESSAGE,"Font Id")));
        }

        validationUtil.validateModules(vendorRequest.getModules());
        long moduleCount = moduleDao.getModuleCount();
        if(vendorRequest.getModules().isEmpty() || vendorRequest.getModules().size() != moduleCount) {
            throw new ValidationException(FIELD_NOT_MATCHING_ERROR_CODE, MessageFormat.format(FIELD_NOT_MATCHING_ERROR_CODE_MESSAGE, "Given Count", "DB Count of modules"));
        }

        validationUtil.validateNotEmpty(vendorRequest.getPreferenceDetails().getDataProviderProtocol(), "Data Provider Protocol");
        validationUtil.validateNotEmpty(vendorRequest.getPreferenceDetails().getDataProviderProtocol(), "Data Provider Protocol");

    }

    private void validateStatus(String status) {
        if (Status.valueOf(status.toUpperCase()).getName().isEmpty()) {
            throw new ValidationException(INVALID_ERROR_CODE, MessageFormat.format(INVALID_ERROR_CODE_MESSAGE, "Status"));
        }
    }

    public void validateContactDetails(ContactDetailsRequest contactDetailsRequest) {
        if (StringUtils.isNotEmpty(contactDetailsRequest.getPhoneNumber())) {
            validationUtil.validatePhoneNumber(contactDetailsRequest.getPhoneNumber());
        }
        if (StringUtils.isNotEmpty(contactDetailsRequest.getEmail())) {
            validationUtil.validateEmail(contactDetailsRequest.getEmail());
        }
    }

    public void validateBankDetails(VendorBankDetails vendorBankDetails, BankDetailsRequest bankDetailsRequest) {
        if(!(vendorBankDetails.getCin().equalsIgnoreCase(bankDetailsRequest.getCin()))) {
            throw new ValidationException(CANNOT_UPDATE_FIELD_CODE, MessageFormat.format(CANNOT_UPDATE_FIELD_MESSAGE, "CIN"));
        }
        if(!(vendorBankDetails.getGst().equalsIgnoreCase(bankDetailsRequest.getGst()))) {
            throw new ValidationException(CANNOT_UPDATE_FIELD_CODE, MessageFormat.format(CANNOT_UPDATE_FIELD_MESSAGE, "GST"));
        }
        if(!(vendorBankDetails.getPan().equalsIgnoreCase(bankDetailsRequest.getPan()))) {
            throw new ValidationException(CANNOT_UPDATE_FIELD_CODE, MessageFormat.format(CANNOT_UPDATE_FIELD_MESSAGE, "PAN"));
        }
    }

    public void validateVendorPreference(PreferenceDetailsRequest preferenceDetailsRequest) {
        if (Optional.of(preferenceDetailsRequest.getTotalMaxAdmin()).isPresent())
            validateMaxVendorAdmins(preferenceDetailsRequest.getTotalMaxAdmin());
    }

    private void validateMaxVendorAdmins(int totalMaxAdmin) {
        if (totalMaxAdmin <= 0) {
            throw new ValidationException(INVALID_ERROR_CODE, MessageFormat.format(INVALID_ERROR_CODE_MESSAGE, "Max Admin Count"));
        }
    }

    public void validatePanAndGST(String pan , String gst){
        validationUtil.validateNotEmpty(pan, "Pan");
        validationUtil.validateNotEmpty(gst, "Gst");
        if(!validationUtil.validatePan(pan)){
            throw new ValidationException(NOT_FOUND_ERROR_CODE, MessageFormat.format(NOT_MATCH_ERROR_MESSAGE_PATTERN, "Pan"));
        }
        if(!validationUtil.validateGst(gst)){
            throw new ValidationException(NOT_FOUND_ERROR_CODE, MessageFormat.format(NOT_MATCH_ERROR_MESSAGE_PATTERN, "Gst"));
        }
    }
}
