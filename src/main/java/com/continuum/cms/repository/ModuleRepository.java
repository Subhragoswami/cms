package com.continuum.cms.repository;

import com.continuum.cms.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ModuleRepository extends JpaRepository<Module, UUID> {

    long count();
}
