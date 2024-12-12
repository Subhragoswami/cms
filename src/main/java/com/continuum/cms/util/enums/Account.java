package com.continuum.cms.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Account {

    SAVING("Savings"),CURRENT("Current");
    private final String name;
}
