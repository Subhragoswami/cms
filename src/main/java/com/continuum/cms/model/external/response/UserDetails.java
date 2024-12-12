package com.continuum.cms.model.external.response;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetails {
    private String mobile;
    private String email;
    private String firstName;
    private String lastName;
    private String userType;
}
