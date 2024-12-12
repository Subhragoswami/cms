package com.continuum.cms.dao;

import com.continuum.cms.entity.*;
import com.continuum.cms.repository.*;
import com.continuum.cms.specifications.ChargingStationSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ChargingStationDao {

    private final ChargingStationRepository chargingStationRepository;
    private final VendorBankDetailsRepository vendorBankDetailsRepository;
    private final VendorRepository vendorRepository;
    private final ChargerDetailRepository chargerDetailRepository;
    private final EVSEDetailRepository evseDetailRepository;
    private final ConnectorDetailRepository connectorDetailRepository;
    private final CarbonCreditsRepository carbonCreditsRepository;

    public Optional<ChargingStation> getChargingStationById(UUID id) {
        return chargingStationRepository.findById(id);
    }

    public Page<ChargingStation> getAllChargingStation(List<String> locations, List<String> cinList, String search, Pageable pageable) {
        Specification<ChargingStation> specification = buildSpecification(locations, cinList);
        if (StringUtils.isEmpty(search)) {
            log.info("fetching all users");
            return chargingStationRepository.findAll(specification, pageable);
        }
        log.info("fetching users based on the search criteria:{}", search);
        return chargingStationRepository.findAll(specification.and(ChargingStationSpecification.withSearch(search)), pageable);
    }

    public Specification<ChargingStation> buildSpecification(List<String> locations, List<String> cinList) {

        Specification<ChargingStation> specification = Specification.where(null);

        if (ObjectUtils.isNotEmpty(locations)) {
            specification = specification.and(ChargingStationSpecification.withLocation(locations));
        }
        if (ObjectUtils.isNotEmpty(cinList)) {
            specification = specification.and(ChargingStationSpecification.withVendorCode(cinList));
        }

        return specification;
    }

    public List<String> getCinByVendorIds(List<UUID> vendorIds) {
        log.info("Fetching list of cins by vendorIds:{} ", vendorIds);
        return vendorBankDetailsRepository.findCinByVendorIds(vendorIds);
    }

    public Optional<Vendor> getVendorByCin(String cin) {
        log.info("Fetching Vendor by cin:{} ", cin);
        return vendorRepository.findByCin(cin);
    }

    public Optional<ChargingStation> findByLocationId(long locationId){
        log.info("Fetching ChargingStation by locationId:{} ", locationId);
        return chargingStationRepository.findByLocationId(locationId);
    }

    public BigDecimal getTotalCC(Long id){
        return carbonCreditsRepository.getTotalCC(id);
    }

    public List<ChargerDetail> getByLocationId(long id){
        log.info("Fetching ChargerDetail by LocationId:{} ", id);
        return chargerDetailRepository.findByLocationId(id);
    }

    public List<EVSEDetail> getByChargerId(String chargerId){
        log.info("Fetching EVSEDetail by chargerId:{} ", chargerId);
        return evseDetailRepository.findByChargerId(chargerId);
    }

    public List<ConnectorDetail> getByEvseId(String evseId) {
        log.info("Fetching ConnectorDetail by evseId:{} ", evseId);
        return connectorDetailRepository.findByEvseId(evseId);
    }

    public List<String> getCityList() {
        return chargingStationRepository.getUniqueCities();
    }
}
