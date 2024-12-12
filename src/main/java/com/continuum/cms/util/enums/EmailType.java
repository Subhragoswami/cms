package com.continuum.cms.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailType {
    RESET_PASSWORD_INITIATE("Password Reset Initiated"),
    RESET_PASSWORD_CONFIRM("Password Reset Confirmation"),
    CHANGE_PASSWORD_CONFIRM("Password Change Confirmation"),
    USER_ONBOARDING("User Onboarding"),
    HELP_SCREEN("Help Screen");

    private final String name;

}
