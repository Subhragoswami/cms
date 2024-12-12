package com.continuum.cms.service;

import static com.continuum.cms.util.AppConstants.RESPONSE_SUCCESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.continuum.cms.entity.FileData;
import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.model.response.FileDataResponse;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.repository.FilesRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

class FileStorageServiceTest {

    @Mock
    private FilesRepository filesRepository;
    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private FileStorageService fileStorageService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

//    @Test
//    void testGetFileDataSuccess() throws IOException {
//        UUID fileId = UUID.randomUUID();
//        String type = "image";
//        String contentType = "image/jpeg";
//        String data = "Test data";
//        Date dateCreated = new Date();
//
//        FileData fileData = new FileData();
//        fileData.setId(fileId);
//        fileData.setType(type);
//        fileData.setContentType(contentType);
//        fileData.setData(data.getBytes());
//        fileData.setDateCreated(dateCreated);
//
//        when(filesRepository.findById(fileId)).thenReturn(Optional.of(fileData));
//
//        FileDataResponse fileDataResponse = FileDataResponse.builder()
//                .id(fileId)
//                .type(type)
//                .contentType(contentType)
//                .data(data)
//                .dateCreated(dateCreated)
//                .build();
//
//        when(mapper.convertValue(fileData, FileDataResponse.class)).thenReturn(fileDataResponse);
//
//        ResponseDto<FileDataResponse> responseDto = fileStorageService.getFileData(fileId);
//
//        assertNotNull(responseDto);
//        assertEquals(RESPONSE_SUCCESS, responseDto.getStatus());
//    }


//    @Test
//    void testGetFileDataNotFound() {
//        UUID fileId = UUID.randomUUID();
//
//        when(filesRepository.findById(fileId)).thenReturn(Optional.empty());
//
//        assertThrows(CMSException.class, () -> fileStorageService.getFileData(fileId));
//    }
}

