package com.continuum.cms.util;

import com.continuum.cms.entity.Vendor;

public class VendorBuilder {

    public static Vendor buildDefaultVendor(String name, String code, String status) {
        return Vendor.builder().name(name).status(status).build();
    }
}
