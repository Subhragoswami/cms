package com.continuum.cms.controller;

import com.continuum.cms.model.request.UserDetailsUpdateRequest;
import com.continuum.cms.model.response.UserDetailsResponse;
import com.continuum.cms.service.UserService;
import com.continuum.cms.model.request.UserPostRequest;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.util.enums.Role;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/superAdmin")
    @Operation(summary = "Creating the Super Admin")
    public ResponseDto<String> saveSuperAdmin(@RequestBody UserPostRequest userPostRequest){
        log.info("Received request to create user:{}", userPostRequest);
        return userService.saveUser(userPostRequest, Role.ROLE_SUPER_ADMIN);
    }

    @PostMapping("/supportAdmin")
    @Operation(summary = "Creating the Support Admin")
    public ResponseDto<String> saveSupportAdmin(@RequestBody UserPostRequest userPostRequest){
        log.info("Received request to create user:{}", userPostRequest);
        return userService.saveUser(userPostRequest, Role.ROLE_SUPPORT_ADMIN);
    }

    @PostMapping("/vendorAdmin")
    @Operation(summary = "Creating the Vendor Admin")
    public ResponseDto<String> saveVendorAdmin(@RequestBody UserPostRequest userPostRequest){
        log.info("Received request to create user:{}", userPostRequest);
        return userService.saveUser(userPostRequest, Role.ROLE_VENDOR_ADMIN);
    }

    @GetMapping
    public ResponseDto<UserDetailsResponse> UserDetails() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        log.info("Getting the user details :{}", userName);
        return userService.getUserDetails(userName);
    }

    @PutMapping(value = "/{userId}")
    public ResponseDto<String> updateDetails(@PathVariable UUID userId,
                                             @RequestBody UserDetailsUpdateRequest userDetailsUpdateRequest) {
        log.info("Received request for update userDetails :{}", userId);
        return userService.updateUserDetails(userId, userDetailsUpdateRequest);
    }

}
