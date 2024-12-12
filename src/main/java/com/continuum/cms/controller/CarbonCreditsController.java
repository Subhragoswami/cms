package com.continuum.cms.controller;

import com.continuum.cms.model.external.requests.CarbonCreditsFilter;
import com.continuum.cms.model.external.response.CarbonCreditsResponse;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.model.response.CarbonCreditDashboardViewResponse;
import com.continuum.cms.service.CarbonCreditsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/carbon-credits")
public class CarbonCreditsController {
    private final CarbonCreditsService carbonCreditsService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseDto<CarbonCreditDashboardViewResponse> getVendorLevelCarbonCreditsList(@PageableDefault(sort = {"vendorName"}, direction = Sort.Direction.ASC) Pageable pageable,
                                                                                          @RequestParam(value = "search", required = false) String search,
                                                                                          @RequestBody(required = false) CarbonCreditsFilter carbonCreditsFilter) {
        log.info("Getting Carbon Credits details for all vendors with search and  filter: {}, {}",search, carbonCreditsFilter);
        return carbonCreditsService.getVendorCarbonCreditsList(carbonCreditsFilter, search, pageable);
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseDto<CarbonCreditsResponse> getTotalCarbonCreditsOfVendors() {
        log.info("Getting Total Carbon Credits Of all Vendors");
        return carbonCreditsService.getTotalCarbonCreditsOfVendors();
    }


    @GetMapping("/vendor")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'SUPER_ADMIN')")
    public ResponseDto<CarbonCreditsResponse> getVendorLevelCarbonCreditsByVendor(HttpServletRequest request, @RequestParam(value = "vendorId", required = false) UUID vendorId) {
        log.info("Getting Carbon Credits details for Vendor");
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return carbonCreditsService.getCarbonCreditsDetailsByVendorId(userName, Optional.ofNullable(vendorId), request.getHeader("Authorization").substring(7));
    }

}
