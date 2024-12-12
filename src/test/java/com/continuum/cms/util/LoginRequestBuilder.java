package com.continuum.cms.util;

import com.continuum.cms.model.request.LoginRequest;

public final class LoginRequestBuilder {

    public static LoginRequest buildLoginRequest(String userName, String password) {
        return LoginRequest.builder().userName(userName).password(password).build();
    }
}
