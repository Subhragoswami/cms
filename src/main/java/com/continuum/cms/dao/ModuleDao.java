package com.continuum.cms.dao;

import com.continuum.cms.entity.Module;
import com.continuum.cms.entity.Vendor;
import com.continuum.cms.entity.VendorModules;
import com.continuum.cms.repository.ModuleRepository;
import com.continuum.cms.repository.VendorModulesRepository;
import com.continuum.cms.repository.VendorRepository;
import com.continuum.cms.repository.VendorUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ModuleDao {

    private final ModuleRepository moduleRepository;
    private final VendorRepository vendorRepository;
    private final VendorModulesRepository vendorModulesRepository;
    private final VendorUserRepository vendorUserRepository;

    public void saveVendorModule(List<VendorModules> vendorModules) {
         vendorModulesRepository.saveAll(vendorModules);
    }

    public Optional<Module> getByModuleId(UUID id) {
        return moduleRepository.findById(id);
    }

    public long getModuleCount() {
        return moduleRepository.count();
    }

    public Optional<Vendor> getVendorById(UUID id) {
        return vendorRepository.findById(id);
    }
    public Integer getCount(){
        return vendorUserRepository.countOfTotalUser();
    }

    public List<VendorModules> getAllModulesByVendorId(UUID vendorId) {
        return vendorModulesRepository.findModuleNamesByVendorId(vendorId);
    }

    public List<String> getActiveModuleNamesByVendorId(UUID vendorId) {
        return vendorModulesRepository.findActiveModuleNamesByVendorId(vendorId);
    }
}
