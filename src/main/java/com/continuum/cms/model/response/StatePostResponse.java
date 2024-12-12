package com.continuum.cms.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatePostResponse {

    private UUID id;
    private String code;
    private String name;
    private boolean active;
    private String countryCode;
}
