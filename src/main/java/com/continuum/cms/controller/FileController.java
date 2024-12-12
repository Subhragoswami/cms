package com.continuum.cms.controller;

import com.continuum.cms.model.response.FileDataResponse;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {
    private final FileStorageService fileStorageService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<FileDataResponse> saveFile(@RequestPart MultipartFile file, @RequestPart String identifier, @RequestPart String fileType) {
        log.info("Getting the file data for the file:{}", file.getName());
        return fileStorageService.saveFile(file, identifier, fileType);

    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'SUPER_ADMIN')")
    public Object getFileData(HttpServletRequest request,
                              @PathVariable UUID id){
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        log.info("Getting the file data for the file:{}", id);
        return fileStorageService.getFileData(id, userName, request.getHeader("Authorization").substring(7));
    }
}
