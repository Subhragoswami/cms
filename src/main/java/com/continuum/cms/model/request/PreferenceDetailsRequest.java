package com.continuum.cms.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Data;

@AllArgsConstructor
@Data
@ToString
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PreferenceDetailsRequest {
    private String productId;
    private int totalMaxAdmin;
    private String dataProviderProtocol;
    private String dataAPIEndpoint;
}