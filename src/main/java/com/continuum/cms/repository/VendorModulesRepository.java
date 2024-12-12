package com.continuum.cms.repository;

import com.continuum.cms.entity.VendorModules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface VendorModulesRepository extends JpaRepository<VendorModules, UUID> {

    @Query("SELECT vm FROM VendorModules vm WHERE vm.vendor.id = :vendorId")
    List<VendorModules> findModuleNamesByVendorId(UUID vendorId);

    @Query("SELECT vm.module.moduleName FROM VendorModules vm WHERE vm.vendor.id = :vendorId AND vm.active = true")
    List<String> findActiveModuleNamesByVendorId(UUID vendorId);

}
