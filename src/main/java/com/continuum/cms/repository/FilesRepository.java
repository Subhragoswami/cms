package com.continuum.cms.repository;

import com.continuum.cms.entity.FileData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FilesRepository extends JpaRepository<FileData, UUID> {

    Optional<FileData> findByTypeAndIdentifier(String type, String identifier);
}
