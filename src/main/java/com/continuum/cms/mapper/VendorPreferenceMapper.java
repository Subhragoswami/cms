package com.continuum.cms.mapper;

import com.continuum.cms.entity.VendorPreference;
import com.continuum.cms.model.request.PreferenceDetailsRequest;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VendorPreferenceMapper {

    private final MapperFactory mapperFactory;

    @Autowired
    public VendorPreferenceMapper() {
        this.mapperFactory = new DefaultMapperFactory.Builder().build();
        configureMapper();
    }

    private void configureMapper() {
        mapperFactory.classMap(PreferenceDetailsRequest.class, VendorPreference.class)
                .byDefault()
                .register();
    }

    public <T> T mapPreferenceDetailsToVendorPreference(Object source, Class<T> targetType) {
        return mapperFactory.getMapperFacade().map(source, targetType);
    }
}
