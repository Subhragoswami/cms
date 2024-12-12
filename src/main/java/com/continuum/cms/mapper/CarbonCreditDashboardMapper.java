package com.continuum.cms.mapper;

import com.continuum.cms.entity.CarbonCreditDashboard;
import com.continuum.cms.model.response.CarbonCreditDashboardViewResponse;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CarbonCreditDashboardMapper {

    private final MapperFactory mapperFactory;

    @Autowired
    public CarbonCreditDashboardMapper() {
        this.mapperFactory = new DefaultMapperFactory.Builder().build();
        configureMapper();
    }

    private void configureMapper() {
        mapperFactory.classMap(CarbonCreditDashboard.class, CarbonCreditDashboardViewResponse.class)
                .byDefault()
                .register();
    }

    public <T> List<T> mapCarbonCreditListToResponse(Page<?> sourceList, Class<T> targetType) {
        return sourceList.stream()
                .map(source -> mapperFactory.getMapperFacade().map(source, targetType))
                .collect(Collectors.toList());
    }
}
