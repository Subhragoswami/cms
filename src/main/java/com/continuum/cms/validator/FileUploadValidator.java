package com.continuum.cms.validator;

import com.continuum.cms.config.CMSServiceConfig;
import com.continuum.cms.exceptions.ValidationException;
import com.continuum.cms.model.response.ErrorDto;
import com.continuum.cms.util.enums.ImageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.continuum.cms.util.ErrorConstants.INVALID_ERROR_CODE;
import static com.continuum.cms.util.ErrorConstants.INVALID_ERROR_CODE_MESSAGE;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileUploadValidator {

    private final CMSServiceConfig cmsConfig;

    public void validate(MultipartFile file, String fileType) {
        log.info("Validating file and filetype");
        List<ErrorDto> errors = new ArrayList<>();

        if (!cmsConfig.getAllowedFileExt().contains(getFileExtension(file))) {
            errors.add(new ErrorDto(INVALID_ERROR_CODE,
                    MessageFormat.format(INVALID_ERROR_CODE_MESSAGE, "fileType", "Valid FileTypes are " + Arrays.toString(cmsConfig.getAllowedFileExt().toArray()))));
        }
        if (file.getSize() > cmsConfig.getMaxFileUploadSize()) {
            errors.add(new ErrorDto(INVALID_ERROR_CODE,
                    MessageFormat.format(INVALID_ERROR_CODE_MESSAGE, "fileSize", "Maximum Allowed size is " + cmsConfig.getMaxFileUploadSize())));
        }
        if(!(ImageType.FONT.getName().equalsIgnoreCase(fileType) || ImageType.LOGO.getName().equalsIgnoreCase(fileType) || ImageType.PROFILEPIC.getName().equalsIgnoreCase(fileType))) {
            errors.add(new ErrorDto(INVALID_ERROR_CODE,
                    MessageFormat.format(INVALID_ERROR_CODE_MESSAGE, "fileType")));
        }

        if (!errors.isEmpty()) throw new ValidationException(errors);

    }

    public String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        return originalFilename != null ? FilenameUtils.getExtension(originalFilename) : null;
    }
}
