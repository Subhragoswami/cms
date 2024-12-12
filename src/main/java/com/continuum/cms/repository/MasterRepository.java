package com.continuum.cms.repository;

import com.continuum.cms.entity.Master;
import com.continuum.cms.model.response.MasterPostResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MasterRepository extends JpaRepository<Master, UUID> {

    @Query("SELECT DISTINCT new com.continuum.cms.model.response.MasterPostResponse(m.stateCode, m.stateName) FROM Master m WHERE m.countryCode = :countryCode")
    List<MasterPostResponse> findDistinctStateCodeAndStateName(@Param("countryCode") String countryCode);

    @Query("SELECT DISTINCT new com.continuum.cms.model.response.MasterPostResponse(m.countryCode, m.countryName) FROM Master m")
    List<MasterPostResponse> findDistinctCountryCodeAndCountryName();
}
