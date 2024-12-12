package com.continuum.cms.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DataProvider {

    OCPP("OCPP"),OCPI("OCPI");

    private final String name;
}
