package com.continuum.cms.repository;

import com.continuum.cms.entity.CarbonCreditDashboard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CarbonCreditDashboardRepository extends JpaRepository<CarbonCreditDashboard, UUID> {

    Page<CarbonCreditDashboard> findAll(Specification<CarbonCreditDashboard> specification, Pageable pageable);
}
