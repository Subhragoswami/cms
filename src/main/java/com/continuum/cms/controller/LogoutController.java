package com.continuum.cms.controller;

import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.service.LogoutService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LogoutController {

    private final LogoutService logoutService;

    @GetMapping("/logout")
    @Operation(summary = "Logout API")
    public ResponseDto<String> logout(HttpServletRequest request) {
        log.info("Received request for Logout.");
        return logoutService.logout(request.getHeader("Authorization").substring(7));
    }
}
