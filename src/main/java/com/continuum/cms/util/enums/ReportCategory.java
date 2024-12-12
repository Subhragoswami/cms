package com.continuum.cms.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportCategory {
    CARBON_CREDIT("Carbon_Credit"),
    VENDOR("Vendor");

    private final String name;
}
