package com.continuum.cms.model.request;

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
public class AddressRequest {
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String country;
    private String pincode;
}

