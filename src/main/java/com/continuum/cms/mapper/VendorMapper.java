package com.continuum.cms.mapper;

import com.continuum.cms.entity.Vendor;
import com.continuum.cms.model.response.VendorFilterListResponse;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VendorMapper {

    private final MapperFactory mapperFactory;

    @Autowired
    public VendorMapper() {
        this.mapperFactory = new DefaultMapperFactory.Builder().build();
        configureMapper();
    }

    private void configureMapper() {
        mapperFactory.classMap(Vendor.class, VendorFilterListResponse.class)
                .byDefault()
                .register();
    }

    public <T> List<T> mapVendorToResponse(List<?> sourceList, Class<T> targetType) {
        return sourceList.stream()
                .map(source -> mapperFactory.getMapperFacade().map(source, targetType))
                .collect(Collectors.toList());
    }
}
