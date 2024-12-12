package com.continuum.cms.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendorDetailsResponse {
    private UUID id;
    private String name;
    private String status;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private String contactFirstName;
    private String contactLastName;
    private String contactNumber;
    private List<String> modules;
    private Date dateCreated;
    private Date dateUpdated;
}
