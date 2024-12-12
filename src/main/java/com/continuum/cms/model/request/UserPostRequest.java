package com.continuum.cms.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPostRequest {
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String phoneNumber;
    private String cin;
}
