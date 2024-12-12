package com.continuum.cms.model.response;

import com.continuum.cms.model.request.BankDetailsRequest;
import com.continuum.cms.model.request.ContactDetailsRequest;
import com.continuum.cms.model.request.PreferenceDetailsRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendorPostResponse {

    private VendorDetailsResponse vendorDetails;
    private ContactDetailsRequest contactDetails;
    private WhitelistingDetailsResponse whitelistingDetails;
    private PreferenceDetailsRequest preferenceDetails;
    private BankDetailsRequest bankDetailsRequest;
    private List<String> modules;
}
