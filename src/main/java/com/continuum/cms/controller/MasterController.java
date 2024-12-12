package com.continuum.cms.controller;

import com.continuum.cms.model.response.MasterPostResponse;
import com.continuum.cms.model.response.ModulesPostResponse;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.service.MasterService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/master")
public class MasterController {

    private final MasterService masterService;

    @GetMapping("/country")
    @Operation(summary = "Get All Countries API")
    public ResponseDto<MasterPostResponse> getAllCountry(@RequestParam(value = "countryCode", required = false) String countryCode) {
        log.info("Received request for get all countries");
        return masterService.getCountryStateData(countryCode);
    }


    @GetMapping("/{name}")
    @Operation(summary = "Get Master API")
    public ResponseDto<String> getMasterData(@PathVariable String name) {
        log.info("Received request for get master data {}", name);
        return masterService.getMasterData(name);
    }

    @GetMapping("/modules")
    @Operation(summary = "Get Modules API")
    public ResponseDto<ModulesPostResponse> getAllModules() {
        log.info("Received request for get all modules data");
        return masterService.getAllModules();
    }
}
