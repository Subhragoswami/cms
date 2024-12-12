package com.continuum.cms.util;

import com.continuum.cms.entity.User;
import com.continuum.cms.entity.UserLoginSessions;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

public final class LoginSessionBuilder {

    public static UserLoginSessions buildLoginSessionRequest(User user) {
        return UserLoginSessions.builder().user(user).loginTime(new Date()).logoutTime(DateUtils.addHours(new Date(), 24)).build();
    }
}
