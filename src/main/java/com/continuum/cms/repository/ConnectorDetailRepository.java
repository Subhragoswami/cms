package com.continuum.cms.repository;

import com.continuum.cms.entity.ConnectorDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ConnectorDetailRepository extends JpaRepository<ConnectorDetail, UUID> {

    @Query("SELECT cd FROM ConnectorDetail cd WHERE cd.evseId = :evseId")
    List<ConnectorDetail> findByEvseId(@Param("evseId") String evseId);
}
