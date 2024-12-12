package com.continuum.cms.dao;

import com.continuum.cms.entity.Module;
import com.continuum.cms.model.response.MasterPostResponse;
import com.continuum.cms.repository.MasterRepository;
import com.continuum.cms.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MasterDao {

    private final MasterRepository masterRepository;
    private final ModuleRepository moduleRepository;

    public List<MasterPostResponse> getALlCountryNames() {
        log.info("fetching all country names.");
        return masterRepository.findDistinctCountryCodeAndCountryName();
    }

    public List<MasterPostResponse> getAllStateByCountryCode(String countryCode) {
        log.info("fetching country and state data based on the country code: {}", countryCode);

        return masterRepository.findDistinctStateCodeAndStateName(countryCode);
    }

    public List<Module> getAllModules() {
        log.info("fetching all modules.");
        return moduleRepository.findAll();
    }

}
