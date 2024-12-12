package com.continuum.cms.controller;

import com.continuum.cms.model.request.HelpRequest;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.service.HelpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class HelpController {

    private final HelpService helpService;

    @PostMapping("/help")
    public ResponseDto<String> helpScreen(@RequestBody HelpRequest helpRequest) {
        log.info("Received request to help user {}", helpRequest);
        return helpService.helpRecordCapture(helpRequest);
    }
}
