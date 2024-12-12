package com.continuum.cms.repository;

import com.continuum.cms.entity.ChargerDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChargerDetailRepository extends JpaRepository<ChargerDetail, UUID> {

    long count();

    List<ChargerDetail> findByLocationId(long locationId);
}
