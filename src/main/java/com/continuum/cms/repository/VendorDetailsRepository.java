package com.continuum.cms.repository;

import com.continuum.cms.entity.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VendorDetailsRepository extends JpaRepository<Vendor, UUID> {
    Page<Vendor> findAll(Specification<Vendor> specification, Pageable pageable);

    @Query("FROM Vendor u WHERE (LOWER(u.name) like %:search%) AND (u.city = :city) AND (u.status = :status)")
    Page<Vendor> getVendorsBySearchAndCityAndStatus(@Param("search")String search, @Param("city")String city, @Param("status")String status, Pageable pageable);

    @Query("FROM Vendor u WHERE (LOWER(u.name) like %:search%) AND (u.city = :city)")
    Page<Vendor> getVendorsBySearchAndCity(@Param("search")String search, @Param("city")String city, Pageable pageable);

    @Query("FROM Vendor u WHERE (LOWER(u.name) like %:search%) AND (u.status = :status)")
    Page<Vendor> getVendorsBySearchAndStatus(@Param("search")String search, @Param("status")String status, Pageable pageable);

    @Query("FROM Vendor u WHERE (LOWER(u.name) like %:search%)")
    Page<Vendor> getVendorsBySearch(@Param("search")String search, Pageable pageable);

    @Query("FROM Vendor u WHERE (u.city = :city) AND (u.status = :status)")
    Page<Vendor> getVendorsByCityAndStatus(@Param("city")String city, @Param("status")String status, Pageable pageable);

    @Query("FROM Vendor u WHERE (u.status = :status)")
    Page<Vendor> getVendorsByStatus(@Param("status")String status, Pageable pageable);

    @Query("FROM Vendor u WHERE (u.city = :city)")
    Page<Vendor> getVendorsByCity(@Param("city")String city, Pageable pageable);

    Page<Vendor> findAll(Pageable pageable);

    List<Vendor> findAllByStatus(String status);
}
