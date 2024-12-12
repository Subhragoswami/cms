package com.continuum.cms.repository;

import com.continuum.cms.entity.Vendor;
import com.continuum.cms.entity.VendorUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VendorUserRepository extends JpaRepository<VendorUser, UUID> {
    List<VendorUser> findByVendorId(UUID vendorId);

    @Query("SELECT vu.vendor.id FROM VendorUser vu JOIN vu.user u WHERE u.userName = :userName")
    Optional<UUID> findVendorIdByUserName(String userName);

    @Query("FROM VendorUser vu JOIN vu.user u WHERE u.userName = :userName")
    VendorUser getVendorDetails(String userName);

    Optional<VendorUser> findByUserId(UUID userId);
    @Query("SELECT COUNT(v.id) FROM VendorUser v")
    Integer countOfTotalUser();

    @Query("SELECT vu.vendor FROM VendorUser vu WHERE vu.user.id = :userId")
    Optional<Vendor> findVendorByUserId(UUID userId);

    @Query("SELECT vu.vendor.id AS vendorId, COUNT(vu) AS userCount FROM VendorUser vu GROUP BY vu.vendor.id")
    List<Object[]> getUserCountsByVendorId();
}
