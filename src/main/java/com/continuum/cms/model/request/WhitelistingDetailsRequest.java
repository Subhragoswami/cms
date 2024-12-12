package com.continuum.cms.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.NoArgsConstructor;

import java.util.UUID;


@AllArgsConstructor
@Data
@ToString
@Builder
@NoArgsConstructor
public class WhitelistingDetailsRequest {
    private String primaryColor;
    private String secondaryColor;
    private String domain;
    private String databaseOnPremise;
    private String vendorEndpoint;
    private String fontFamily;
    private UUID fontFile;
}
