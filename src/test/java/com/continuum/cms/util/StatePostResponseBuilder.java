package com.continuum.cms.util;

import com.continuum.cms.model.response.StatePostResponse;

public class StatePostResponseBuilder {

    public static StatePostResponse buildStateResponseRequest(String countryCode, String stateName, String stateCode) {
        return StatePostResponse.builder().countryCode(countryCode).name(stateName).code(stateCode).build();
    }
}
