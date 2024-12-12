package com.continuum.cms.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportStatus {
    INITIATED("Initiated"),
    PROCESSING("Processing"),
    AVAILABLE("Available"),
    FAILED("Failed");

    private final String name;
}
