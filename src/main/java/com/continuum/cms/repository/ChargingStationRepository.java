package com.continuum.cms.repository;

import com.continuum.cms.entity.ChargingStation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChargingStationRepository extends JpaRepository<ChargingStation, UUID> {
    Page<ChargingStation> findAll(Specification<ChargingStation> specification, Pageable pageable);

    Optional<ChargingStation> findByLocationId(long locationId);

    @Query("select distinct(city) from ChargingStation where city is not null and city <> ''")
    List<String> getUniqueCities();
}
