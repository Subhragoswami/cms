package com.continuum.cms.util;

import com.continuum.cms.model.response.MasterPostResponse;

public class MasterResponseBuilder {

    public static MasterPostResponse buildMasterRequest(String code, String name) {
        return MasterPostResponse.builder().code(code).name(name).build();
    }
}
