package com.continuum.cms.dao;

import com.continuum.cms.entity.VendorPreference;
import com.continuum.cms.repository.VendorPreferenceRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
@RequiredArgsConstructor
@Getter
public class VendorPreferenceDao {

    private final VendorPreferenceRepository vendorPreferenceRepository;



    public Optional<VendorPreference> getVendorPreferenceByVendorId(UUID vendorId) {
        return vendorPreferenceRepository.findByVendorId(vendorId);
    }

    public VendorPreference saveVendorPreference(VendorPreference vendorPreference) {
        return vendorPreferenceRepository.save(vendorPreference);
    }

    public List<Object[]> getAllVendorCINAndEndPoint(){
        return vendorPreferenceRepository.findAllVendorCINAndEndPoint();
    }
}
