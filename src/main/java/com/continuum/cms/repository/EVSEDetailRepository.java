package com.continuum.cms.repository;

import com.continuum.cms.entity.EVSEDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EVSEDetailRepository extends JpaRepository<EVSEDetail, UUID> {

    List<EVSEDetail> findByChargerId(String chargerId);
}
