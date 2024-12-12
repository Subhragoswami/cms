package com.continuum.cms.repository;

import com.continuum.cms.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface VendorRepository extends JpaRepository<Vendor, UUID> {

    long count();

    @Query("SELECT v FROM Vendor v LEFT JOIN VendorBankDetails vb ON v.id = vb.vendor.id WHERE vb.cin = :cin")
    Optional<Vendor> findByCin(@Param("cin") String cin);
}
