package com.continuum.cms.dao;

import com.continuum.cms.entity.ChargingSession;
import com.continuum.cms.model.external.requests.ChargingSessionFilter;
import com.continuum.cms.repository.ChargingSessionRepository;
import com.continuum.cms.specifications.ChargingSessionSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ChargingSessionDao{
    private final ChargingSessionRepository chargingSessionRepository;

    public Page<ChargingSession> getAllChargingSession(ChargingSessionFilter chargingSessionFilter, String search, Pageable pageable, List<String> vendorCodes) {
        if(StringUtils.isEmpty(search) && ObjectUtils.isEmpty(chargingSessionFilter)){
            log.info("Fetching all the data from the DB: {}",pageable);
            return chargingSessionRepository.findAll(pageable);
        }
        if(ObjectUtils.isEmpty(chargingSessionFilter)){
            log.info("fetching users based on the search criteria:{}", search);
            Specification<ChargingSession> searchSpecification = new ChargingSessionSpecification.SearchSpecification(search);
            return  chargingSessionRepository.findAll(searchSpecification, pageable);
        }

        Specification<ChargingSession> specification = buildSpecification(search , chargingSessionFilter, vendorCodes);
        log.info("fetching users based on the filter criteria:{}", chargingSessionFilter);
        return chargingSessionRepository.findAll(specification,pageable);
    }
    private Specification<ChargingSession> buildSpecification(String search, ChargingSessionFilter chargingSessionFilter, List<String> vendorCodes) {
        Specification<ChargingSession> specification = Specification.where(null);
        log.info("Building specification with criteria : {}, {}", chargingSessionFilter, vendorCodes);
        if (StringUtils.isNotEmpty(search)) {
            Specification<ChargingSession> searchSpec = new ChargingSessionSpecification.SearchSpecification(search);
            specification = specification.and(searchSpec);
        }
        if (ObjectUtils.isNotEmpty(chargingSessionFilter)){
            Specification<ChargingSession> stationSpec = new ChargingSessionSpecification.FilterSpecification(chargingSessionFilter.getCities(),
                    chargingSessionFilter.getStartDate(), chargingSessionFilter.getEndDate(), chargingSessionFilter.getStationIds(), vendorCodes);
            specification = specification.and(stationSpec);
        }
        return specification;
    }

}
