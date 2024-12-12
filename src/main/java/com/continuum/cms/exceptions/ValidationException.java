package com.continuum.cms.exceptions;

import com.continuum.cms.model.response.ErrorDto;
import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException {

    private String errorCode;
    private String errorMessage;
    private List<ErrorDto> errorMessages;

    public ValidationException(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ValidationException(List<ErrorDto> errorMessages) {
        this.errorMessages = errorMessages;
    }

}
