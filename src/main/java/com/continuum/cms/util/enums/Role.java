package com.continuum.cms.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

    ROLE_SUPER_ADMIN("ROLE_SUPER_ADMIN", 1), ROLE_VENDOR_ADMIN("ROLE_VENDOR_ADMIN", 2),
    ROLE_SUPPORT_ADMIN("ROLE_SUPPORT_ADMIN", 3);

    private final String name;
    private final int value;

}
