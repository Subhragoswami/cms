package com.continuum.cms.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Data
@ToString
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WhitelistingDetailsResponse {

    private String primaryColor;
    private String secondaryColor;
    private String domain;
    private String databaseOnPremise;
    private String vendorEndpoint;
    private String fontFamily;
    private UUID fontFile;
}
