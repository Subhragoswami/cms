package com.continuum.cms.model.response;

import com.continuum.cms.model.external.response.Coordinates;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class ChargingStationResponse {

    private UUID id;
    private long locationId;
    private String name;
    private Coordinates coordinates;
    private String parkingType;
    private String email;
    private String phone;
    private String image;
    private String street1;
    private String street2;
    private String city;
    private String state;
    private String pincode;
    private String country;
    private String status;
    private String hash;
    private UUID vendorId;
    private String vendorCode;
    private String vendorName;
}
