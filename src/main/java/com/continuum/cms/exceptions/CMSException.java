package com.continuum.cms.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CMSException extends RuntimeException {

    private final String errorCode;
    private final String errorMessage;

}

