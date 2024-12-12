package com.continuum.cms.repository;

import com.continuum.cms.entity.VendorWhitelisting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VendorWhitelistingRepository extends JpaRepository<VendorWhitelisting, UUID> {

    Optional<VendorWhitelisting> findByVendorId(UUID vendorId);

}
