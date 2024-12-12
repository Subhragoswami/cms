package com.continuum.cms.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetailsResponse {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String status;
    private String phoneNumber;
    private Date dateCreated;
    private Date dateUpdated;
    private UUID profilePic;
    private UUID vendorId;

}
