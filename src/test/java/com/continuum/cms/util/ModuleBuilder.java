package com.continuum.cms.util;

import com.continuum.cms.entity.Module;

public class ModuleBuilder {

    public static Module buildDefaultModule(String name, String description) {
        return Module.builder().moduleName(name).moduleDescription(description).build();
    }
}
