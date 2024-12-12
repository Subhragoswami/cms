package com.continuum.cms.dao;

import com.continuum.cms.entity.VendorBankDetails;
import com.continuum.cms.model.request.ReportFilter;
import com.continuum.cms.repository.VendorBankDetailsRepository;
import com.continuum.cms.specifications.VendorSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;


import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Slf4j
@RequiredArgsConstructor
public class VendorBankDao {

    private final VendorBankDetailsRepository vendorBankDetailsRepository;

    public Optional<VendorBankDetails> getVendorBankByVendorId(UUID vendorId) {
        return vendorBankDetailsRepository.findByVendorId(vendorId);
    }

    public VendorBankDetails saveVendorBankingDetails(VendorBankDetails vendorBankDetails) {
        return vendorBankDetailsRepository.save(vendorBankDetails);
    }

    public List<VendorBankDetails>  getByVendorIdIn(List<UUID> vendorIds){
        log.info("Fetching all VendorBankDetails by vendorIds.");
        return vendorBankDetailsRepository.findByVendorIdIn(vendorIds);
    }

    public List<VendorBankDetails> getAllVendorDetails(ReportFilter reportFilter) {
        if (ObjectUtils.isEmpty(reportFilter)) {
            log.info("Fetching all the data from the DB: {}", "vendor data");
            return vendorBankDetailsRepository.findAll();
        }
        Specification<VendorBankDetails> specification = buildSpecification(reportFilter.getVendors(), reportFilter.getStartDate(), reportFilter.getEndDate());
        log.info("fetching vendor and bank details based on the filter criteria:{},{},{}",reportFilter.getStartDate(),reportFilter.getEndDate(),reportFilter.getVendors());
        return vendorBankDetailsRepository.findAll(specification);
    }
    public Specification<VendorBankDetails> buildSpecification(List<UUID> vendors, Date startDate, Date endDate) {

        Specification<VendorBankDetails> specification = Specification.where(null);
        return specification.and(VendorSpecifications.filterVendorDetails(vendors, startDate, endDate));
    }
}
