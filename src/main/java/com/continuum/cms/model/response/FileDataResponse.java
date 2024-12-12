package com.continuum.cms.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileDataResponse {
    private UUID id;
    private String name;
    private String type;
    private String identifier;
    private String contentType;
    private String data;
    private UUID fileId;
    private Date dateCreated;

}
