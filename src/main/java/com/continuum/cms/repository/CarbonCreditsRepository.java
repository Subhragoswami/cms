package com.continuum.cms.repository;

import com.continuum.cms.entity.CarbonCreditDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface CarbonCreditsRepository extends JpaRepository<CarbonCreditDetail, UUID> {

    @Query("SELECT COALESCE(SUM(c.creditScorePoints), 0.0) FROM CarbonCreditDetail c WHERE c.stationId = :stationId")
    BigDecimal getTotalCC(@Param("stationId") Long stationId);

    @Query("SELECT CarbonCreditDetail from CarbonCreditDetail c where c.compositeId.vendorCode = :vendorCode")
    Optional<CarbonCreditDetail> findByVendorCode(@Param("vendorCode") String vendorCode);
}
