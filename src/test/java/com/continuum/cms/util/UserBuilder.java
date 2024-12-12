package com.continuum.cms.util;

import com.continuum.cms.entity.User;

public final class UserBuilder {

    public static User buildDefaultUser(String userName, String password, String status) {
        return User.builder().userName(userName).password(password).status(status).loginAttempts(0).build();
    }
}
