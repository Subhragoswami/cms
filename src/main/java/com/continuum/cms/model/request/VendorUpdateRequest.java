package com.continuum.cms.model.request;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Data
@ToString
@Builder
@NoArgsConstructor
public class VendorUpdateRequest {

    private String name;
    private String status;
    private AddressRequest address;
    private ContactDetailsRequest contactDetails;
    private WhitelistingDetailsRequest whitelistingDetails;
    private PreferenceDetailsRequest preferenceDetails;
    private BankDetailsRequest bankDetailsRequest;
    private List<ModuleRequest> modules;
}
