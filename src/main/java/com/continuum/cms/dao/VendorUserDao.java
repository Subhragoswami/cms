package com.continuum.cms.dao;

import com.continuum.cms.entity.VendorUser;
import com.continuum.cms.repository.VendorUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Repository
@Slf4j
public class VendorUserDao {
    private final VendorUserRepository vendorUserRepository;

    public List<VendorUser> getUsersByVendorId(UUID vendorId){
       return vendorUserRepository.findByVendorId(vendorId);
    }

    public Optional<VendorUser> getUserByUserId(UUID userId){
        return vendorUserRepository.findByUserId(userId);
    }

    public List<Object[]> getUserCountsByVendorId(){
        return vendorUserRepository.getUserCountsByVendorId();
    }
}
