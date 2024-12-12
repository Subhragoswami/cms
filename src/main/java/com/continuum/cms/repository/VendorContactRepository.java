package com.continuum.cms.repository;

import com.continuum.cms.entity.VendorContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VendorContactRepository extends JpaRepository<VendorContact, UUID> {

    Optional<VendorContact> findByVendorId(UUID vendorId);
}
