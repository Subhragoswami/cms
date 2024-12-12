package com.continuum.cms.dao;

import com.continuum.cms.entity.CarbonCreditDetail;
import com.continuum.cms.entity.CarbonCreditDashboard;
import com.continuum.cms.repository.CarbonCreditsRepository;
import com.continuum.cms.repository.CarbonCreditDashboardRepository;
import com.continuum.cms.specifications.VendorCarbonCreditViewSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CarbonCreditDao {
    private final CarbonCreditsRepository carbonCreditsRepository;
    private final CarbonCreditDashboardRepository carbonCreditDashboardViewRepository;

    public List<CarbonCreditDetail> getAllCarbonCreditsData(){
        log.info("fetching all Carbon Credits data");
        return carbonCreditsRepository.findAll();
    }

    public Page<CarbonCreditDashboard> getAllVendorCarbonCredits(List<UUID> vendorIds, String search, Pageable pageable) {
        Specification<CarbonCreditDashboard> specification = buildSpecificationForVendorCarbonCredits(vendorIds);
        if (StringUtils.isEmpty(search)) {
            log.info("fetching all details for Vendor Carbon Credits");
            return carbonCreditDashboardViewRepository.findAll(specification, pageable);
        }
        log.info("fetching users based on the search criteria:{}", search);
        return carbonCreditDashboardViewRepository.findAll(specification.and(VendorCarbonCreditViewSpecifications.withSearch(search)), pageable);
    }

    public Specification<CarbonCreditDashboard> buildSpecificationForVendorCarbonCredits(List<UUID> vendorId) {

        Specification<CarbonCreditDashboard> specification = Specification.where(null);

        if (ObjectUtils.isNotEmpty(vendorId)) {
            specification = specification.and(VendorCarbonCreditViewSpecifications.withVendorIds(vendorId));
        }

        return specification;
    }
}
