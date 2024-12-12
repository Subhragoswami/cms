package com.continuum.cms.service;

import com.continuum.cms.auth.security.JwtService;
import com.continuum.cms.entity.FileData;
import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.model.response.FileDataResponse;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.repository.FilesRepository;
import com.continuum.cms.service.external.VendorClientService;
import com.continuum.cms.util.BinaryToBase64;
import com.continuum.cms.util.ErrorConstants;
import com.continuum.cms.util.MiscUtil;
import com.continuum.cms.util.enums.Role;
import com.continuum.cms.validator.FileUploadValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.continuum.cms.util.AppConstants.RESPONSE_FAILURE;
import static com.continuum.cms.util.AppConstants.RESPONSE_SUCCESS;
import static com.continuum.cms.util.ErrorConstants.INVALID_ERROR_CODE;
import static com.continuum.cms.util.ErrorConstants.INVALID_ERROR_CODE_MESSAGE;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {
    private final VendorClientService vendorClientService;
    private final JwtService jwtService;
    @Lazy
    @Autowired
    private MiscUtil miscUtil;
    private final FilesRepository filesRepository;
    private final FileUploadValidator fileUploadValidator;
    private final ObjectMapper mapper;


    public ResponseDto<FileDataResponse> saveFile(MultipartFile file, String identifier, String fileType) {
        fileUploadValidator.validate(file, fileType);
        try {
            Optional<FileData> fileDataFromDB = filesRepository.findByTypeAndIdentifier(fileType, identifier);
            FileData fileData;
            if (fileDataFromDB.isPresent()) {
                fileData = fileDataFromDB.get();
                fileData.setData(file.getBytes());
                fileData.setName(file.getOriginalFilename());
                fileData.setContentType(file.getContentType());
            } else {
                fileData = FileData.builder().type(fileType).identifier(identifier).contentType(file.getContentType())
                        .data(file.getBytes()).name(file.getOriginalFilename())
                        .build();
            }

            FileData savedFileData = filesRepository.save(fileData);

            FileDataResponse fileDataResponse = mapper.convertValue(savedFileData, FileDataResponse.class);
            return ResponseDto.<FileDataResponse>builder()
                    .status(RESPONSE_SUCCESS)
                    .data(List.of(fileDataResponse))
                    .build();
        } catch (IOException e) {
            log.error("There is some error in saving file {}", e.getMessage());
            throw new CMSException(INVALID_ERROR_CODE, INVALID_ERROR_CODE_MESSAGE);
        }
    }


    public Object getFileData(UUID id, String userName, String token) {
        log.info("Extracting roles from token for user: {}", userName);
        String roles = jwtService.extractRoles(token);
        if (roles.equals(Role.ROLE_SUPER_ADMIN.getName())) {
            log.info("User has SUPER_ADMIN role. Fetching file data for SUPER_ADMIN");
            return getFileDataForSuperAdmin(id);
        } else if (roles.equals(Role.ROLE_VENDOR_ADMIN.getName())) {
            log.info("User has VENDOR_ADMIN role. Fetching file data for VENDOR_ADMIN");
            ResponseDto<FileDataResponse> responseResponseDto = getFileDataForVendorAdmin(id, userName);
            if (responseResponseDto.getStatus() == RESPONSE_SUCCESS) {
                return responseResponseDto;
            } else if (responseResponseDto.getStatus() == RESPONSE_FAILURE) {
                return getFileDataForSuperAdmin(id);
            }
        }
        log.error("Invalid role for user: {}", userName);
        throw new CMSException(ErrorConstants.NOT_FOUND_ERROR_CODE, MessageFormat.format(ErrorConstants.NOT_FOUND_ERROR_MESSAGE, "Roles"));
    }

    private ResponseDto<FileDataResponse> getFileDataForSuperAdmin(UUID id){
        log.info("getting fileData for super admin");
        FileData fileData = filesRepository.findById(id).orElseThrow(
                () -> new CMSException(ErrorConstants.INVALID_ERROR_CODE, MessageFormat.format(ErrorConstants.INVALID_ERROR_CODE_MESSAGE, "File Id")));
        log.info("Converting fileData to Base64");
        String base64Encoded = BinaryToBase64.convertToBase64(fileData.getData());
        FileDataResponse fileDataResponse = mapper.convertValue(fileData, FileDataResponse.class);
        fileDataResponse.setData(base64Encoded);
        return ResponseDto.<FileDataResponse>builder().status(RESPONSE_SUCCESS).data(List.of(fileDataResponse)).build();
    }

    private ResponseDto<FileDataResponse> getFileDataForVendorAdmin(UUID id, String userName){
        log.info("Getting fileData for VENDOR_ADMIN with file ID: {} and userName: {}", id, userName);
        String vendorEndpoint =  miscUtil.validateUserNameAndGetVendorEndPoint(userName);
        log.info("Validated userName and got vendorEndpoint: {}", vendorEndpoint);
        return vendorClientService.getFileData(id, vendorEndpoint);
    }
}
