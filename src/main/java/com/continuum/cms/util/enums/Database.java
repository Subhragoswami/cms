package com.continuum.cms.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Database {

    ON_PREM("On-prem"),CLOUD("Cloud");
    private final String name;
}
