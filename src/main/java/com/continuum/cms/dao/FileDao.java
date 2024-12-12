package com.continuum.cms.dao;

import com.continuum.cms.entity.FileData;
import com.continuum.cms.repository.FilesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FileDao {

    private final FilesRepository filesRepository;

    public Optional<FileData> findFileByTypeAndIdentifier(String type, String identifier) {
        return filesRepository.findByTypeAndIdentifier(type, identifier);
    }

    public void saveFile(FileData fileData) {
        filesRepository.save(fileData);
    }

}
