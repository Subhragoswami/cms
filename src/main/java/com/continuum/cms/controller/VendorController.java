package com.continuum.cms.controller;

import com.continuum.cms.model.request.VendorFilterRequest;
import com.continuum.cms.model.request.VendorModulePostRequest;
import com.continuum.cms.model.request.VendorRequest;
import com.continuum.cms.model.request.VendorUpdateRequest;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.model.response.VendorDetailsResponse;
import com.continuum.cms.model.response.VendorFilterListResponse;
import com.continuum.cms.model.response.VendorPostResponse;
import com.continuum.cms.service.VendorDetailsService;
import com.continuum.cms.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@Slf4j
@RequestMapping("/vendor")
@RequiredArgsConstructor
public class VendorController {

    private final VendorDetailsService vendorDetailsService;
    private final VendorService vendorService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Adding Vendor Details")
    public ResponseDto<String> addVendor(@RequestBody VendorRequest vendorRequest) {
        log.info("Received request:{}", vendorRequest);
        return vendorDetailsService.addVendor(vendorRequest);
    }

    @PutMapping(path = "/{vendorId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Updating the Vendor details")
    public ResponseDto<String> updateVendorDetails(@PathVariable UUID vendorId, @RequestBody VendorUpdateRequest vendorRequest) {
        log.info("Received request to Update Basic Vendor Details for vendorId:{}, Request:{}", vendorId, vendorRequest);
        return vendorDetailsService.updateVendorDetails(vendorId, vendorRequest);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Getting Vendor details for Super Admin")
    public ResponseDto<VendorPostResponse> getVendorDetailsForSuperAdmin(@PathVariable UUID id) {
        log.info("Received request to get Vendor Details for Super Admin. vendorId:{}", id);
        return vendorDetailsService.getVendorDetailById(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('VENDOR_ADMIN')")
    @Operation(summary = "Getting Vendor details for Vendor Admin")
    public ResponseDto<VendorPostResponse> getVendorAdminForVendorAdmin( ){
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        log.info("Received request to get Vendor Details for Vendor Admin for username: {}", userName);
        return vendorDetailsService.getVendorDetailsByUserName(userName);
    }

    @PostMapping("/list")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Getting All Vendor details")
    public ResponseDto<VendorDetailsResponse> getVendorDetails(@RequestParam(name = "search", required = false) String search,
                                                               @PageableDefault(sort = {"dateCreated"}, direction = Sort.Direction.DESC) Pageable pageable,
                                                               @RequestBody(required = false) VendorFilterRequest vendorFilterRequest
                                                               ) {
        log.info("Received request to get Vendor Details.");
        return vendorDetailsService.getAllVendors(vendorFilterRequest, search, pageable);
    }

    @PostMapping("/configureVendorModules")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Post API")
    public ResponseDto<String> configureVendorModules(@RequestBody VendorModulePostRequest vendorModulePostRequest) {
        log.info("Received request for configure vendor module {}", vendorModulePostRequest);
        return vendorService.saveVendorModule(vendorModulePostRequest);
    }

    @GetMapping("/dashboard")
    public Object getVendorDashboardDetails(){
        log.info("Recieved request to get vendor dashboard details");
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return vendorService.getVendorDashboardDetails(userName);
    }

    @GetMapping("/filter")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseDto<VendorFilterListResponse> getFilterData(@RequestParam(name = "status") String status) {
        log.info("Received request to get all vendor by status {}", status);
        return vendorDetailsService.getVendorFilterData(status);
    }
}
