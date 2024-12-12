package com.continuum.cms.dao;

import com.continuum.cms.entity.VendorContact;
import com.continuum.cms.repository.VendorContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class VendorContactDao {
    private final VendorContactRepository vendorContactRepository;

    public Optional<VendorContact> getVendorContactByVendorId(UUID vendorId) {
        return vendorContactRepository.findByVendorId(vendorId);
    }

    public VendorContact saveVendorContactDetails(VendorContact vendorContactDB) {
        return vendorContactRepository.save(vendorContactDB);
    }

    public Map<UUID, VendorContact> getVendorContactsById(Set<UUID> vendorIds) {
        return vendorContactRepository.findAllById(vendorIds).stream().collect(Collectors.toMap(VendorContact::getId, Function.identity()));
    }
}
