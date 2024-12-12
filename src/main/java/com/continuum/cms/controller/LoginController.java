package com.continuum.cms.controller;

import com.continuum.cms.model.request.LoginRequest;
import com.continuum.cms.model.request.PasswordChangeRequest;
import com.continuum.cms.model.response.LoginResponse;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    @Operation(summary = "Login API")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        log.info("Received request for login {}", loginRequest);
        return loginService.login(loginRequest, true);
    }

    @PostMapping("/login/reLogin")
    @Operation(summary = "Re-Login API")
    public LoginResponse reLogin(@RequestBody LoginRequest loginRequest) {
        log.info("Received request for reLogin {}", loginRequest);
        return loginService.login(loginRequest, false);
    }

    @GetMapping("/password/initiateReset")
    @Operation(summary = "Reset Password Initiation API")
    public ResponseDto<String> initiateResetPassword(@RequestParam String userName) {
        log.info("Received request for reset password {}", userName);
        return loginService.initiateResetPassword(userName);
    }

    @PostMapping("/password/reset")
    @Operation(summary = "ResetPassword API")
    public ResponseDto<String> resetPassword(HttpServletRequest request, @RequestBody PasswordChangeRequest passwordChangeRequest) {
        log.info("Received request for reset password {}", passwordChangeRequest);
        return loginService.resetPassword(request.getHeader("passwordResetToken"), passwordChangeRequest);
    }
    @PostMapping("/password/change")
    @Operation(summary = "ChangePassword API")
    public ResponseDto<String> changePassword(HttpServletRequest request, @RequestBody PasswordChangeRequest passwordChangeRequest){
        log.info("Received request for change password {}", passwordChangeRequest);
        return loginService.changePassword(request.getHeader("Authorization").substring(7), passwordChangeRequest);
    }

}
