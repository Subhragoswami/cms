package com.continuum.cms.dao;

import com.continuum.cms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class DashboardDao {

    private final VendorRepository vendorRepository;
    private final UserRoleRepository userRoleRepository;
    private final ChargingSessionRepository chargingSessionRepository;
    private final ChargerDetailRepository chargerDetailsRepository;

    public int getVendorCount() {
        return (int) vendorRepository.count();
    }

    public long getAdminCount() {
        return userRoleRepository.count();
    }
    public Integer getTotalUsedEnergy() {
        return (Integer) chargingSessionRepository.getTotalUsedEnergy();
    }
    public long getChargingSessionCount() {
        return chargingSessionRepository.count();
    }
    public long getChargerDetailsCount() {
        return chargerDetailsRepository.count();
    }
    public double getCountTotalUtilization() {
        return chargingSessionRepository.countTotalUtilization();
    }

}
