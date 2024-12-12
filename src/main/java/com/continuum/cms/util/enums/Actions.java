package com.continuum.cms.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Actions {
    CHANGE_PASSWORD("CHANGE_PASSWORD", "1"), RESET_PASSWORD("RESET_PASSWORD", "2");
    private final String name;
    private final String value;
}
