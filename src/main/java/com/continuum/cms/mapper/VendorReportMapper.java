package com.continuum.cms.mapper;

import com.continuum.cms.entity.VendorBankDetails;
import com.continuum.cms.util.VendorCarbonCreditEntry;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
public class VendorReportMapper {

    private final MapperFactory mapperFactory;
    private final MapperFacade mapperFacade;

    @Autowired
    public VendorReportMapper() {
        this.mapperFactory = new DefaultMapperFactory.Builder().build();
        this.mapperFacade = mapperFactory.getMapperFacade();
        configureMapper();
    }

    private void configureMapper() {
        mapperFactory.classMap(VendorBankDetails.class, VendorCarbonCreditEntry.class)
                .field("vendor.name", "vendorName")
                .field("vendor.id", "vendorId")
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(VendorBankDetails source, VendorCarbonCreditEntry target, MappingContext context) {
                        super.mapAtoB(source, target, context);
                        target.setTotalPoints(BigDecimal.valueOf(0));
                    }
                })
                .byDefault()
                .mapNulls(false)
                .register();
    }

    public List<VendorCarbonCreditEntry> mapToVendorReport(List<VendorBankDetails> reportList) {
        log.info("Mapping report list.");
        return mapperFacade.mapAsList(reportList, VendorCarbonCreditEntry.class);
    }
}
