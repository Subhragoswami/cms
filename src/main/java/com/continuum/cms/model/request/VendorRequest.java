package com.continuum.cms.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Data;

import java.util.List;


@AllArgsConstructor
@Data
@ToString
@Builder
@NoArgsConstructor
public class VendorRequest {
    private UserDetailsRequest userDetails;
    private String name;
    private String status;
    private AddressRequest address;
    private ContactDetailsRequest contactDetails;
    private WhitelistingDetailsRequest whitelistingDetails;
    private PreferenceDetailsRequest preferenceDetails;
    private BankDetailsRequest bankDetailsRequest;
    private List<ModuleRequest> modules;

}