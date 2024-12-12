package com.continuum.cms.dao;

import com.continuum.cms.entity.FileData;
import com.continuum.cms.entity.VendorWhitelisting;
import com.continuum.cms.repository.FilesRepository;
import com.continuum.cms.repository.VendorWhitelistingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Slf4j
@RequiredArgsConstructor
public class VendorWhiteListingDao {
    private final VendorWhitelistingRepository vendorWhitelistingRepository;
    private final FilesRepository filesRepository;

    public Optional<VendorWhitelisting> getVendorWhiteListingByVendorId(UUID vendorId) {
        return vendorWhitelistingRepository.findByVendorId(vendorId);
    }

    public VendorWhitelisting saveVendorWhiteListingDetails(VendorWhitelisting vendorWhitelisting) {
        return vendorWhitelistingRepository.save(vendorWhitelisting);
    }
    public Optional<FileData> getByFileId(UUID fileId){
        return filesRepository.findById(fileId);
    }
}
