package com.continuum.cms.util;

import com.continuum.cms.entity.Master;

public class CountryBuilder {

    public static Master buildCountryRequest(String code, String name) {
        return Master.builder().countryCode(code).countryName(name).build();
    }
}
