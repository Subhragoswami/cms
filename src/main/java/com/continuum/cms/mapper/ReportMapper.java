package com.continuum.cms.mapper;

import com.continuum.cms.entity.Report;
import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.model.request.ReportFilter;
import com.continuum.cms.model.response.ReportResponse;

import com.continuum.cms.util.DateUtil;
import com.continuum.cms.util.ErrorConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
public class ReportMapper {
    private final MapperFactory mapperFactory;
    private final MapperFacade mapperFacade;
    private final ObjectMapper objectMapper;

    @Autowired
    public ReportMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.mapperFactory = new DefaultMapperFactory.Builder().build();
        this.mapperFacade = mapperFactory.getMapperFacade();
        configureMapper();
    }

    private void configureMapper() {
        mapperFactory.classMap(Report.class, ReportResponse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Report source, ReportResponse target, MappingContext context) {
                        try {
                            ReportFilter reportFilter = objectMapper.readValue(source.getFilter(), ReportFilter.class);
                            if (ObjectUtils.isNotEmpty(reportFilter.getStartDate()) && ObjectUtils.isNotEmpty(reportFilter.getEndDate())) {
                                target.setStartDate(DateUtil.formatDateToString(reportFilter.getStartDate()));
                                target.setEndDate(DateUtil.formatDateToString(reportFilter.getEndDate()));
                            }
                            target.setDateCreated(DateUtil.formatDateToString(source.getDateCreated()));
                        } catch (JsonProcessingException e) {
                            log.info("Error while parsing string to object : {}", e.getMessage());
                            throw new CMSException(ErrorConstants.PARSE_ERROR_CODE, ErrorConstants.PARSE_ERROR_MESSAGE);
                        }
                    }
                })
                .byDefault()
                .mapNulls(false)
                .register();
    }

    public List<ReportResponse> mapToReportList(List<Report> reportList) {
        log.info("Mapping report list.");
        return mapperFacade.mapAsList(reportList, ReportResponse.class);
    }
}
