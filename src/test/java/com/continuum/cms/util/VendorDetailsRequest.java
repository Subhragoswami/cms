package com.continuum.cms.util;

import com.continuum.cms.model.request.AddressRequest;
import com.continuum.cms.model.request.BankDetailsRequest;
import com.continuum.cms.model.request.ContactDetailsRequest;
import com.continuum.cms.model.request.ModuleRequest;
import com.continuum.cms.model.request.PreferenceDetailsRequest;
import com.continuum.cms.model.request.VendorRequest;
import com.continuum.cms.model.request.WhitelistingDetailsRequest;

import java.util.List;
import java.util.UUID;


public final class VendorDetailsRequest {
    public static VendorRequest createVendorRequest() {
        UUID uuid = UUID.fromString("48a1403c-c085-43b7-9958-7752993bafe2");
        ModuleRequest moduleRequest = new ModuleRequest();
        moduleRequest.setId(uuid);
        moduleRequest.setActive(true);

        AddressRequest address = new AddressRequest();
        address.setAddress1("123 Main St");
        address.setAddress2("Suite 100");
        address.setState("California");
        address.setCountry("USA");
        address.setPincode("12345");

        ContactDetailsRequest contactDetails = new ContactDetailsRequest();
        contactDetails.setFirstName("John");
        contactDetails.setLastName("Doe");
        contactDetails.setEmail("john@example.com");
        contactDetails.setPhoneNumber("9635774148L");

        PreferenceDetailsRequest preferenceDetails = new PreferenceDetailsRequest();
        preferenceDetails.setProductId("PROD123");
        preferenceDetails.setTotalMaxAdmin(5);
        preferenceDetails.setDataProviderProtocol("OCPP");

        WhitelistingDetailsRequest whitelistingDetails = new WhitelistingDetailsRequest();
        whitelistingDetails.setFontFamily("Arial");
        whitelistingDetails.setPrimaryColor("#FFFFFF");
        whitelistingDetails.setSecondaryColor("#000000");

        BankDetailsRequest bankDetailsRequest = new BankDetailsRequest();
        bankDetailsRequest.setCin("1234");

        VendorRequest vendorRequest = new VendorRequest();
        vendorRequest.setName("Example Vendor");
        vendorRequest.setAddress(address);
        vendorRequest.setContactDetails(contactDetails);
        vendorRequest.setPreferenceDetails(preferenceDetails);
        vendorRequest.setWhitelistingDetails(whitelistingDetails);
        vendorRequest.setBankDetailsRequest(bankDetailsRequest);
        vendorRequest.setModules(List.of(moduleRequest));

        return vendorRequest;
    }
}
