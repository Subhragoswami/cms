package com.continuum.cms.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {

    ACTIVE("Active"), INACTIVE("InActive"),
    BLOCKED("Blocked"), EXPIRED("Expired"),
    COMPLETED("Completed"), PENDING("Pending"),
    PASSWORD_EXPIRED("PasswordExpired");

    private final String name;

}
