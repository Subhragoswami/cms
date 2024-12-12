package com.continuum.cms.model.external.response;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VendorReportDetailsResponse {
    private String stationName;
    private String Location;
    private String userName;
    private String email;
    private String phoneNumber;
    private String vehicleName;
    private String vehicleNumber;
    private UUID id;
    private String customerTransactionId;
    private int certificateNumber;
    private String status;
    private Date date;
    private String category;
    private Date startAt;
    private Date stopAt;
    private String usedEnergy;
    private double latitude;
    private double longitude;
    private double carbonCreditGenerated;
}
