package com.continuum.cms.repository;

import com.continuum.cms.entity.VendorBankDetails;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VendorBankDetailsRepository extends JpaRepository<VendorBankDetails, UUID> {

    Optional<VendorBankDetails> findByCin(String cin);

    Optional<VendorBankDetails> findByVendorId(UUID vendorId);
    List<VendorBankDetails> findAll(Specification<VendorBankDetails> specification);

    @Query("SELECT v.cin FROM VendorBankDetails v WHERE v.vendor.id IN :vendorIds")
    List<String> findCinByVendorIds(@Param("vendorIds") List<UUID> vendorIds);

    List<VendorBankDetails> findByVendorIdIn(List<UUID> vendorIds);

}
