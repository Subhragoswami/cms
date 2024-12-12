package com.continuum.cms.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsUpdateRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private UUID fileId;
}
