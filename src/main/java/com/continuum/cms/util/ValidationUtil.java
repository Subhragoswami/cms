package com.continuum.cms.util;

import com.continuum.cms.exceptions.ValidationException;
import com.continuum.cms.model.request.ModuleRequest;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Pattern;

import static com.continuum.cms.util.AppConstants.GST_PATTERN;
import static com.continuum.cms.util.AppConstants.PAN_PATTERN;
import static com.continuum.cms.util.ErrorConstants.*;

@Component
public class ValidationUtil {

    private final Pattern TEN_DIGIT_PHONE_NUMBER_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");

    public void validateNotEmpty(Object value, String fieldName) throws ValidationException {
        if (value == null) {
            throw new ValidationException(MANDATORY_ERROR_CODE,
                    MessageFormat.format(MANDATORY_ERROR_MESSAGE, fieldName));
        } else if (value instanceof String && StringUtils.isEmpty((String) value)) {
            throw new ValidationException(MANDATORY_ERROR_CODE,
                    MessageFormat.format(MANDATORY_ERROR_MESSAGE, fieldName));
        } else if (value instanceof List && CollectionUtils.isEmpty((List<?>) value)) {
            throw new ValidationException(MANDATORY_ERROR_CODE,
                    MessageFormat.format(MANDATORY_ERROR_MESSAGE, fieldName));
        }
    }

    public void validateModules(List<ModuleRequest> value) throws ValidationException {
        if (value == null) {
            throw new ValidationException(MANDATORY_ERROR_CODE,
                    MessageFormat.format(MANDATORY_ERROR_MESSAGE, "Modules"));
        }
        for(ModuleRequest moduleRequest : value ) {
            if(moduleRequest.getId() == null) {
                throw new ValidationException(MANDATORY_ERROR_CODE,
                        MessageFormat.format(MANDATORY_ERROR_MESSAGE, "Module Id"));
            }
        }
    }

    public void validatePhoneNumber(String phoneNumber) {
        if (!TEN_DIGIT_PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches()) {
            throw new ValidationException(INVALID_ERROR_CODE, MessageFormat.format(INVALID_ERROR_CODE_MESSAGE, "Phone number"));
        }
    }

    public void validateEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException(INVALID_ERROR_CODE, MessageFormat.format(INVALID_ERROR_CODE_MESSAGE, "Email Id"));
        }
    }
    public boolean validatePan(String panNumber){
        return Pattern.compile(PAN_PATTERN).matcher(panNumber).matches();
    }
    public boolean validateGst(String gstNumber){
        return Pattern.compile(GST_PATTERN).matcher(gstNumber).matches();
    }

}