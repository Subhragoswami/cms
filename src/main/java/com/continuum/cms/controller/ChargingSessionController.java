package com.continuum.cms.controller;

import com.continuum.cms.model.external.requests.ChargingSessionFilter;
import com.continuum.cms.service.ChargingSessionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/charging-session")
public class ChargingSessionController {
    private final ChargingSessionService chargingSessionService;


    @PostMapping
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'SUPER_ADMIN')")
    public Object getChargingSessionDetails(HttpServletRequest request, @PageableDefault(sort = {"startAt"}, direction = Sort.Direction.DESC) Pageable pageable,
                                            @RequestParam(value = "search", required = false) String search,
                                            @RequestBody(required = false) ChargingSessionFilter chargingSessionFilter) {
        log.info("Getting Charging Session details with search:{}, filter:{}", search, chargingSessionFilter);
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return chargingSessionService.getChargingSessionDetails(pageable, search, chargingSessionFilter, userName, request.getHeader("Authorization").substring(7));
    }

    @GetMapping("/pull")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'SUPER_ADMIN')")
    public Object pullChargingSessionsData(@RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate, @RequestParam(value="vendorId", required = false) UUID vendorId) {
        log.info("Pulling Charging Sessions Data from Date:{}, to Date:{}", startDate, endDate);
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return chargingSessionService.pullChargingSessionsData(startDate, endDate, userName, Optional.ofNullable(vendorId));
    }

    @GetMapping("/transaction")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'SUPER_ADMIN')")
    public Object getAllTransactionsData(HttpServletRequest request, @PageableDefault(sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable, @RequestParam(value = "vendorId", required = false) UUID vendorId) {
        log.info("Getting All Charging Sessions Data");
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return chargingSessionService.getAllTransactionsData(userName, request.getHeader("Authorization").substring(7), Optional.ofNullable(vendorId),  pageable);
    }

    @GetMapping("/transaction/{transactionId}")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'SUPER_ADMIN')")
    public Object getTransactionById(@PathVariable(value= "transactionId")String transactionId) {
        log.info("Getting Charging Sessions Data for transactionId:{}", transactionId);
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return chargingSessionService.getTransactionById(userName, transactionId);
    }
}
