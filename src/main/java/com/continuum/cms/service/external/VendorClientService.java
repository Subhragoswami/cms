package com.continuum.cms.service.external;


import com.continuum.cms.model.external.requests.ChargingSessionFilter;
import com.continuum.cms.model.external.requests.ChargingStationFilter;
import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.model.external.requests.VendorCarbonCreditFilterRequest;
import com.continuum.cms.model.external.requests.VendorReportFilterRequest;
import com.continuum.cms.model.response.FileDataResponse;
import com.continuum.cms.model.response.ResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.text.MessageFormat;
import java.util.*;

import static com.continuum.cms.util.ErrorConstants.CLIENT_ERROR_CODE;
import static com.continuum.cms.util.ErrorConstants.CLIENT_ERROR_MESSAGE;


@Component
@Slf4j
@AllArgsConstructor
public class VendorClientService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public Object getChargingStationData(String vendorEndpoint, Pageable pageable, String search, ChargingStationFilter chargingStationFilter) {
        log.info("Fetching ChargingStation Data from the the endPoint:{} ", vendorEndpoint);
        return fetchDataFromVendorEndpoint(vendorEndpoint, pageable, search, chargingStationFilter, "/charging-station");
    }

    public Object getVendorChargingSessionDetails(String vendorEndpoint, Pageable pageable, String search, ChargingSessionFilter chargingSessionFilter) {
        log.info("Fetching ChargingSession Data from the the endPoint:{} ", vendorEndpoint);
        return fetchDataFromVendorEndpoint(vendorEndpoint, pageable, search, chargingSessionFilter, "/charging-session");
    }

    public Object getStationDetailsById(long id, String vendorEndpoint){
        log.info("Fetching all station details from the the endPoint:{} ", vendorEndpoint);
        try {
            HttpHeaders httpHeaders = prepareHttpHeaders();
            HttpEntity<Object> httpEntity = new HttpEntity<>(null, httpHeaders);
            URI uri = URI.create(vendorEndpoint + "/charging-station" + "/" + id);
            return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, Object.class).getBody();
        } catch (Exception e) {
            log.error("Error while fetching Charging Station data from the vendor endpoint: {}", e.getMessage());
            throw new CMSException(CLIENT_ERROR_CODE, MessageFormat.format(CLIENT_ERROR_MESSAGE, e.getMessage()));

        }
    }
    public Object getVendorCityList(String vendorEndpoint) {
        log.info("Fetching CityList from the the endPoint:{} ", vendorEndpoint);
        try {
            HttpHeaders httpHeaders = prepareHttpHeaders();
            HttpEntity<Object> httpEntity = new HttpEntity<>(null, httpHeaders);

            Map<String, String> urlParams = new HashMap<>();

            URI uri = UriComponentsBuilder.fromUriString(vendorEndpoint)
                    .path("/charging-station/city").buildAndExpand(urlParams)
                    .toUri();

            return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, Object.class).getBody();
        } catch (Exception e) {
            log.error("There is some error in fetching data from the vendor endpoint: {}", e.getMessage());
            throw new CMSException(CLIENT_ERROR_CODE, MessageFormat.format(CLIENT_ERROR_MESSAGE, e.getMessage()));
        }
    }

    public Object pullChargingSessionsData(String vendorEndpoint, String startDate, String endDate) {
        log.info("Pulling ChargingSession Data from the the endPoint:{} ", vendorEndpoint);
        try {
            HttpHeaders httpHeaders = prepareHttpHeaders();
            HttpEntity<Object> httpEntity = new HttpEntity<>(null, httpHeaders);

            URI uri = UriComponentsBuilder.fromUriString(vendorEndpoint)
                    .path("/data/pull")
                    .queryParamIfPresent("startDate", Optional.of(startDate))
                    .queryParamIfPresent("endDate", Optional.of(endDate))
                    .build()
                    .toUri();

            return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, Object.class).getBody();
        } catch (Exception e) {
            log.error("There is some error in fetching data from the vendor endpoint: {}", e.getMessage());
            throw new CMSException(CLIENT_ERROR_CODE, MessageFormat.format(CLIENT_ERROR_MESSAGE, e.getMessage()));
        }
    }

    public ResponseDto<FileDataResponse> getFileData(UUID id, String vendorEndpoint){
        log.info("getting file data from the endpoint: {}", vendorEndpoint);
        try {
            HttpHeaders httpHeaders = prepareHttpHeaders();
            HttpEntity<Object> httpEntity = new HttpEntity<>(null, httpHeaders);
            URI uri = URI.create(vendorEndpoint + "/file" + "/" + id);
            ResponseDto<FileDataResponse> fileDataResponse = (ResponseDto<FileDataResponse>) objectMapper.convertValue(restTemplate.exchange(uri, HttpMethod.GET, httpEntity, Object.class).getBody(), ResponseDto.class);
            return fileDataResponse;
        }catch (Exception e) {
            log.error("There is some error in fetching data from the vendor endpoint: {}", e.getMessage());
            throw new CMSException(CLIENT_ERROR_CODE, MessageFormat.format(CLIENT_ERROR_MESSAGE, e.getMessage()));
        }
    }
    public Object getVendorDashboardData(String vendorEndpoint){
        try {
            HttpHeaders httpHeaders = prepareHttpHeaders();
            HttpEntity<Object> httpEntity = new HttpEntity<>(null, httpHeaders);

            Map<String, String> urlParams = new HashMap<>();

            URI uri = UriComponentsBuilder.fromUriString(vendorEndpoint)
                    .path("/dashboard").buildAndExpand(urlParams)
                    .toUri();

            return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, Object.class).getBody();
        }catch (Exception e) {
            log.error("There is some error in fetching data: {}", e.getMessage());
            throw new CMSException(CLIENT_ERROR_CODE, MessageFormat.format(CLIENT_ERROR_MESSAGE, e.getMessage()));
        }
    }
    public Object getVendorReport(String vendorEndpoint, VendorCarbonCreditFilterRequest vendorCarbonCreditFilterRequest, UUID userId, UUID vendorId){
        try {
            HttpHeaders httpHeaders = prepareHttpHeaders();
            HttpEntity<Object> httpEntity = new HttpEntity<>(vendorCarbonCreditFilterRequest, httpHeaders);
            URI uri = UriComponentsBuilder.fromUriString(vendorEndpoint).path("/report")
                    .queryParamIfPresent("userId", Optional.of(userId))
                    .queryParamIfPresent("vendorId", Optional.of(vendorId))
                    .build()
                    .toUri();
            return restTemplate.exchange(uri, HttpMethod.POST, httpEntity, Object.class).getBody();
        } catch (Exception e) {
            log.error("There is some error in fetching data: {}", e.getMessage());
            throw new CMSException(CLIENT_ERROR_CODE, MessageFormat.format(CLIENT_ERROR_MESSAGE, e.getMessage()));
        }
    }

    public Object getVendorReportData(UUID id, String vendorEndpoint){
        log.info("Fetching all station details from the the endPoint:{} ", vendorEndpoint);
        try {
            HttpHeaders httpHeaders = prepareHttpHeaders();
            HttpEntity<Object> httpEntity = new HttpEntity<>(null, httpHeaders);
            URI uri = URI.create(vendorEndpoint + "/report" +  "/" + id);
            return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, Object.class).getBody();
        } catch (Exception e) {
            log.error("Error while fetching data from the vendor endpoint: {}", e.getMessage());
            throw new CMSException(CLIENT_ERROR_CODE, MessageFormat.format(CLIENT_ERROR_MESSAGE, e.getMessage()));

        }
    }
    public Object getAllVendorReportData(String vendorEndpoint, Pageable pageable, VendorReportFilterRequest vendorReportFilterRequest){
        log.info("Fetching all station details from the the endPoint:{} ", vendorEndpoint);
        try {
            HttpHeaders httpHeaders = prepareHttpHeaders();
            HttpEntity<Object> httpEntity = new HttpEntity<>(vendorReportFilterRequest, httpHeaders);
            Sort.Order order = pageable.getSort().get().findFirst().get();
            URI uri = UriComponentsBuilder.fromUriString(vendorEndpoint).path("/report/list")
                    .queryParamIfPresent("page", Optional.ofNullable(pageable.getPageNumber()))
                    .queryParamIfPresent("size", Optional.ofNullable(pageable.getPageSize()))
                    .queryParamIfPresent("sort", Optional.of(order.getProperty() + "," + order.getDirection())).build().toUri();
            return restTemplate.exchange(uri, HttpMethod.POST, httpEntity, Object.class).getBody();
        } catch (Exception e) {
            log.error("Error while fetching data from the vendor endpoint: {}", e.getMessage());
            throw new CMSException(CLIENT_ERROR_CODE, MessageFormat.format(CLIENT_ERROR_MESSAGE, e.getMessage()));
        }
    }
    public Object fetchDataFromVendorEndpoint(String vendorEndpoint, Pageable pageable, String search, Object filter, String path) {
        log.info("Fetching all details from the the endPoint:{} ", vendorEndpoint);
        try {
            HttpHeaders httpHeaders = prepareHttpHeaders();
            HttpEntity<Object> httpEntity = new HttpEntity<>(filter, httpHeaders);
            Sort.Order order = pageable.getSort().get().findFirst().get();
            URI uri = UriComponentsBuilder.fromUriString(vendorEndpoint).path(path)
                    .queryParamIfPresent("page", Optional.ofNullable(pageable.getPageNumber()))
                    .queryParamIfPresent("size", Optional.ofNullable(pageable.getPageSize()))
                    .queryParamIfPresent("sort", Optional.of(order.getProperty() + "," + order.getDirection()))
                    .queryParamIfPresent("search", Optional.ofNullable(search)).build().toUri();


            return restTemplate.exchange(uri, HttpMethod.POST, httpEntity, Object.class).getBody();
        } catch (Exception e) {
            log.error("There is some error in fetching data from the vendor endpoint: {}", e.getMessage());
            throw new CMSException(CLIENT_ERROR_CODE, MessageFormat.format(CLIENT_ERROR_MESSAGE, e.getMessage()));
        }
    }

    public Object getAllTransactionsData(String vendorEndpoint, Pageable pageable) {
        log.info("Fetching all Transaction details from the the endPoint:{} ", vendorEndpoint);
        try {
            HttpHeaders httpHeaders = prepareHttpHeaders();
            HttpEntity<Object> httpEntity = new HttpEntity<>(null, httpHeaders);
            Sort.Order order = pageable.getSort().get().findFirst().get();
            URI uri = UriComponentsBuilder.fromUriString(vendorEndpoint)
                    .path("/transaction")
                    .queryParamIfPresent("page", Optional.ofNullable(pageable.getPageNumber()))
                    .queryParamIfPresent("size", Optional.ofNullable(pageable.getPageSize()))
                    .queryParamIfPresent("sort", Optional.of(order.getProperty() + "," + order.getDirection()))
                    .build()
                    .toUri();

            return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, Object.class).getBody();
        } catch (Exception e) {
            log.error("There is some error in fetching data from the vendor endpoint: {}", e.getMessage());
            throw new CMSException(CLIENT_ERROR_CODE, MessageFormat.format(CLIENT_ERROR_MESSAGE, e.getMessage()));
        }
    }

    public Object getTransactionById(String vendorEndpoint, String transactionId) {
        log.info("Fetching Transaction details from the the endPoint:{} ", vendorEndpoint);
        try {
            HttpHeaders httpHeaders = prepareHttpHeaders();
            HttpEntity<Object> httpEntity = new HttpEntity<>(null, httpHeaders);

            URI uri = UriComponentsBuilder.fromUriString(vendorEndpoint)
                    .path("/transaction/" + transactionId)
                    .build()
                    .toUri();

            return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, Object.class).getBody();
        } catch (Exception e) {
            log.error("There is some error in fetching data from the vendor endpoint: {}", e.getMessage());
            throw new CMSException(CLIENT_ERROR_CODE, MessageFormat.format(CLIENT_ERROR_MESSAGE, e.getMessage()));
        }
    }
    public ResponseDto<BigDecimal> getVendorCarbonCredits(String vendorEndpoint){
        log.info("Fetching all station details from the the endPoint:{} ", vendorEndpoint);
        try {
            HttpHeaders httpHeaders = prepareHttpHeaders();
            HttpEntity<Object> httpEntity = new HttpEntity<>(null, httpHeaders);
            URI uri = URI.create(vendorEndpoint + "/carbon-credits");
            Object response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, Object.class).getBody();
            return objectMapper.convertValue(response, new TypeReference<ResponseDto<BigDecimal>>() {});

        } catch (Exception e) {
            log.error("Error while fetching Charging Station data from the vendor endpoint: {}", e.getMessage());
            throw new CMSException(CLIENT_ERROR_CODE, MessageFormat.format(CLIENT_ERROR_MESSAGE, e.getMessage()));

        }
    }

    protected HttpHeaders prepareHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

}

