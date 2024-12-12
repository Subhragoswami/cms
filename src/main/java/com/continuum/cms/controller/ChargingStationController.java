package com.continuum.cms.controller;

import com.continuum.cms.model.external.requests.ChargingStationFilter;
import com.continuum.cms.service.ChargingStationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/charging-station")
public class ChargingStationController {

    private final ChargingStationService chargingStationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'SUPER_ADMIN')")
    public Object getChargingStationDetails(HttpServletRequest request, @PageableDefault(sort = {"dateCreated"}, direction = Sort.Direction.DESC) Pageable pageable,
                                            @RequestParam(value = "search", required = false) String search,
                                            @RequestBody( required = false) ChargingStationFilter chargingStationFilter){
        log.info("Getting Charging Station details with search:{} and filter:{}",search, chargingStationFilter);
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return chargingStationService.getChargingStationDetails(request.getHeader("Authorization").substring(7),pageable, search, chargingStationFilter, userName);
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'SUPER_ADMIN')")
    public Object getChargingStationDetailsById(HttpServletRequest request, @PathVariable long id) {
        log.info("Getting Charging Station details for id:{}", id);
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return chargingStationService.getChargingStationDetailsByLocationId(request.getHeader("Authorization").substring(7), id, userName);
    }

    @GetMapping("/city")
    public Object getCityList(HttpServletRequest request){
        log.info("Request for getting cityList");
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return chargingStationService.getCityList(request.getHeader("Authorization").substring(7), userName);
    }

}
