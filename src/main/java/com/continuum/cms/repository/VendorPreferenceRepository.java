package com.continuum.cms.repository;

import com.continuum.cms.entity.VendorPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VendorPreferenceRepository extends JpaRepository<VendorPreference, UUID> {

    Optional<VendorPreference> findByVendorId(UUID vendorId);

    @Query(value = "SELECT vad.cin, vp.data_api_endpoint " +
            "FROM vendor_preference vp " +
            "JOIN vendor_account_details vad " +
            "ON vp.vendor_id = vad.vendor_id", nativeQuery = true)
    List<Object[]> findAllVendorCINAndEndPoint();


}

