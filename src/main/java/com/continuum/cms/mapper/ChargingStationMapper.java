package com.continuum.cms.mapper;

import com.continuum.cms.entity.ChargerDetail;
import com.continuum.cms.entity.ChargingStation;
import com.continuum.cms.entity.ConnectorDetail;
import com.continuum.cms.entity.EVSEDetail;
import com.continuum.cms.model.external.response.ChargerDetailsResponse;
import com.continuum.cms.model.external.response.ConnectorDetailsResponse;
import com.continuum.cms.model.external.response.EvseDetailsResponse;
import com.continuum.cms.model.response.*;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChargingStationMapper {

    private final MapperFactory mapperFactory;
    private final MapperFacade mapperFacade;

    @Autowired
    public ChargingStationMapper() {
        this.mapperFactory = new DefaultMapperFactory.Builder().build();
        this.mapperFacade = mapperFactory.getMapperFacade();
        configureMapper();
    }

    private void configureMapper() {
        mapperFactory.classMap(ChargingStation.class, ChargingStationResponse.class)
                .field("latitude", "coordinates.latitude")
                .field("longitude", "coordinates.longitude")
                .field("compositeId.vendorCode", "vendorCode")
                .byDefault()
                .register();
        mapperFactory.classMap(ChargingStation.class, ChargingStationDetailsResponse.class)
                .field("latitude", "coordinates.latitude")
                .field("longitude", "coordinates.longitude")
                .byDefault()
                .register();
    }

    public <T> List<T> mapChargingStationListToResponse(Page<?> sourceList, Class<T> targetType) {
        return sourceList.stream()
                .map(source -> mapperFactory.getMapperFacade().map(source, targetType))
                .collect(Collectors.toList());
    }

    public ChargingStationDetailsResponse mapToChargingStationToResponse(ChargingStation chargingStation, List<ChargerDetail> chargerDetails, List<EVSEDetail> evseDetails, List<ConnectorDetail> connectorDetails) {
        ChargingStationDetailsResponse chargingStationResponse = mapperFactory.getMapperFacade().map(chargingStation, ChargingStationDetailsResponse.class);
        chargingStationResponse.setChargerDetailsResponse(mapChargerDetails(chargerDetails));
        chargingStationResponse.setEvseDetailsResponse(mapEvseDetails(evseDetails));
        chargingStationResponse.setConnectorDetailsResponse(mapConnectorDetails(connectorDetails));
        return chargingStationResponse;
    }

    private List<ChargerDetailsResponse> mapChargerDetails(List<ChargerDetail> chargerDetailsList) {
        return mapperFactory.getMapperFacade().mapAsList(chargerDetailsList, ChargerDetailsResponse.class);
    }

    private List<EvseDetailsResponse> mapEvseDetails(List<EVSEDetail> evseDetailsList) {
        return evseDetailsList.stream()
                .map(evseDetails -> mapperFactory.getMapperFacade().map(evseDetails, EvseDetailsResponse.class))
                .collect(Collectors.toList());
    }
    private List<ConnectorDetailsResponse> mapConnectorDetails(List<ConnectorDetail> connectorDetailsList) {
        return connectorDetailsList.stream()
                .map(connectorDetails -> mapperFactory.getMapperFacade().map(connectorDetails, ConnectorDetailsResponse.class))
                .collect(Collectors.toList());
    }

}
