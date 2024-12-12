package com.continuum.cms.model.response;

import lombok.*;

@Data
@ToString
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
