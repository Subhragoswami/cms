package com.continuum.cms.util;

import com.continuum.cms.entity.UserPasswordAudit;

public class UserPasswordAuditBuilder {

    public static UserPasswordAudit buildUserPasswordAuditRequest(String status) {
        return UserPasswordAudit.builder().status(status).build();
    }
}
