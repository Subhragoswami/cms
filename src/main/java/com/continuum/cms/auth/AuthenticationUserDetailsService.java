package com.continuum.cms.auth;

import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.service.UserService;
import com.continuum.cms.util.ErrorConstants;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
public class AuthenticationUserDetailsService {
    private final UserService userService;

    public AuthenticationUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    public UserDetailsService userDetailsService() {
        return username -> userService.getUserByUserName(username)
                .orElseThrow(() -> new CMSException(ErrorConstants.NOT_FOUND_ERROR_CODE, MessageFormat.format(ErrorConstants.NOT_FOUND_ERROR_MESSAGE, "User")));
    }
}
