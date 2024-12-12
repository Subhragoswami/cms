package com.continuum.cms.service;

import com.continuum.cms.dao.ReportDao;
import com.continuum.cms.dao.UserDao;
import com.continuum.cms.dao.VendorBankDao;
import com.continuum.cms.entity.*;
import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.mapper.ReportMapper;
import com.continuum.cms.mapper.VendorReportMapper;
import com.continuum.cms.model.request.ReportFilter;
import com.continuum.cms.model.response.ReportResponse;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.util.*;
import com.continuum.cms.validator.ReportValidation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static com.continuum.cms.util.AppConstants.*;
import static com.continuum.cms.util.ErrorConstants.REPORT_ERROR_MESSAGE;
import static com.continuum.cms.util.enums.ReportStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class ReportService {
    private final ReportStorageService reportStorageService;
    private final VendorBankDao vendorBankDao;
    private final UserDao userDao;
    private final ReportDao reportDao;
    private final ObjectMapper objectMapper;
    private final ReportMapper reportMapper;
    private final VendorReportMapper vendorReportMapper;
    private final Executor asyncExecutor;
    public CompletableFuture<ResponseDto<String>> generateReport(String userName, ReportFilter reportFilter) {
        ReportValidation.reportRequestValidation(reportFilter);
        User user = userDao.getByUserName(userName)
                .orElseThrow(() -> new CMSException(ErrorConstants.NOT_FOUND_ERROR_CODE, MessageFormat.format(ErrorConstants.NOT_FOUND_ERROR_MESSAGE, "User")));
        String category = reportFilter.getCategories().get(0).toLowerCase();
        Report report= reportDao.saveReports(buildSaveReports(category, getFilterString(reportFilter), user.getId(), INITIATED.getName()));
        log.info("Calling Async Method....");
        downloadCSVFileAsync(reportFilter, report);

        return CompletableFuture.completedFuture(ResponseDto.<String>builder()
                .status(RESPONSE_SUCCESS)
                .data(List.of("Report generation initiated"))
                .build());

    }
    @Async("asyncExecutor")
    public void downloadCSVFileAsync(ReportFilter reportFilter, Report report) {
        CompletableFuture.supplyAsync(() -> {
            log.info("Started processing report on thread: {}", Thread.currentThread().getName());
            try {
                ReportValidation.reportRequestValidation(reportFilter);
                String category = reportFilter.getCategories().get(0).toLowerCase();
                reportDao.updateReportStatusById(report.getId(), PROCESSING.getName(), null);
                return switch (category) {
                    case CARBON_CREDIT -> handleCarbonCreditReport(reportFilter, report);
                    case VENDOR -> handleVendorReport(reportFilter, report);
                    default -> throw new CMSException(ErrorConstants.NOT_FOUND_ERROR_CODE,
                            MessageFormat.format(ErrorConstants.NOT_FOUND_ERROR_MESSAGE, "category"));
                };
            } catch (Exception e) {
                log.info("Error while processing report : {}", e.getMessage());
                reportDao.updateReportStatusById(report.getId(), FAILED.getName(), null);
                throw new CMSException(ErrorConstants.REPORT_ERROR_CODE, REPORT_ERROR_MESSAGE);
            }
        }).thenAccept(fileId -> {
            if(ObjectUtils.isEmpty(fileId)){
                reportDao.updateReportStatusById(report.getId(), FAILED.getName(), fileId);
            }else{
                reportDao.updateReportStatusById(report.getId(), AVAILABLE.getName(), fileId);
            }
        });
    }

    private UUID handleCarbonCreditReport(ReportFilter reportFilter, Report report) {
        log.info("Generating report for carbon credit: {}", CATEGORY_CARBON_CREDIT);
        List<VendorBankDetails> vendorBankDetailsList;
        List<String> vendorCodes = new ArrayList<>();

        if (!CollectionUtils.isEmpty(reportFilter.getVendors())) {
            vendorBankDetailsList = vendorBankDao.getByVendorIdIn(reportFilter.getVendors());
            vendorCodes = vendorBankDetailsList.stream()
                    .map(VendorBankDetails::getCin)
                    .collect(Collectors.toList());
        }
        log.info("Fetching charging session data.");
        List<ChargingSession> chargingSessionList = reportDao.getChargingSessionDetails(reportFilter, vendorCodes);
        log.info("Calculating total carbon credit generated.");
        BigDecimal totalCarbonCreditPoints = chargingSessionList.stream()
                .map(ChargingSession::getCarbonCreditDetail)
                .filter(details -> ObjectUtils.isNotEmpty(details) && ObjectUtils.isNotEmpty(details.getCreditScorePoints()))
                .map(CarbonCreditDetail::getCreditScorePoints)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Calculating per vendor total carbon credit generated.");
        List<VendorCarbonCreditEntry> totalCarbonCreditPointsByVendor = chargingSessionList.stream()
                .filter(session -> ObjectUtils.isNotEmpty(session.getCarbonCreditDetail()) && ObjectUtils.isNotEmpty(session.getVendorBankDetails()))
                .collect(Collectors.groupingBy(
                        session -> session.getVendorBankDetails().getVendor().getName() + "," + session.getVendorBankDetails().getVendor().getId(),
                        Collectors.mapping(
                                session -> session.getCarbonCreditDetail().getCreditScorePoints(),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    String[] vendorInfo = entry.getKey().split(",");
                    String vendorName = vendorInfo[0];
                    UUID vendorId = UUID.fromString(vendorInfo[1]);
                    BigDecimal totalPoints = entry.getValue();
                    return new VendorCarbonCreditEntry(vendorName, vendorId, totalPoints);
                })
                .collect(Collectors.toList());

        List<String> carbonCreditVendorCsvContent = new ArrayList<>();
        List<String> carbonCreditSessionCsvContent = new ArrayList<>();
        if(CollectionUtils.isEmpty(chargingSessionList) && !CollectionUtils.isEmpty(reportFilter.getVendors())){
            List<VendorBankDetails> vendorBankDetails = vendorBankDao.getByVendorIdIn(reportFilter.getVendors());
            totalCarbonCreditPointsByVendor = vendorReportMapper.mapToVendorReport(vendorBankDetails);
        }
        reportStorageService.csvRecordsFromVendorCarbonCredit(carbonCreditVendorCsvContent, totalCarbonCreditPointsByVendor);
        reportStorageService.csvRecordsFromCarbonCreditPerSession(carbonCreditSessionCsvContent, chargingSessionList);
        CarbonCreditCSVContent carbonCreditCSVContent = buildCSVContent(totalCarbonCreditPoints, carbonCreditVendorCsvContent, carbonCreditSessionCsvContent);
        return reportStorageService.downloadCSVFileForCarbonCredit(reportFilter, carbonCreditCSVContent, CARBON_CREDIT_FILE_NAME, report);
    }

    private UUID handleVendorReport(ReportFilter reportFilter, Report report) {
        log.info("Generating report for vendor: {}", CATEGORY_VENDOR);
        List<VendorBankDetails> vendorBankDetails = vendorBankDao.getAllVendorDetails(reportFilter);
            List<String> vendorCsvContent = new ArrayList<>();
            reportStorageService.csvRecordsFromVendor(vendorCsvContent, vendorBankDetails);
            return reportStorageService.downloadCSVFileForVendor(reportFilter, vendorCsvContent,VENDOR_FILE_NAME, report);
    }

    public ResponseDto<ReportResponse> reportList(ReportFilter reportFilter, String search, Pageable pageable) {
        log.info("Fetching report list with criteria: {}", reportFilter);
        ReportValidation.dateRangeFilter(reportFilter);
        Page<Report> reports = reportDao.getReportList(reportFilter, search, pageable);
        return ResponseDto.<ReportResponse>builder()
                .status(RESPONSE_SUCCESS)
                .data(reportMapper.mapToReportList(reports.getContent()))
                .count(reports.stream().count())
                .total(reports.getTotalElements())
                .build();
    }
    private String getFilterString(ReportFilter reportFilter) {
        try {
            log.info("Parsing object to string.");
            return objectMapper.writeValueAsString(buildFilterReports(reportFilter));
        } catch (JsonProcessingException e) {
            log.info("Error while parsing object to string : {}", e.getMessage());
            throw new CMSException(ErrorConstants.PARSE_ERROR_CODE, ErrorConstants.PARSE_ERROR_MESSAGE);
        }
    }

    private ReportFilter buildFilterReports(ReportFilter reportFilter) {
        return ReportFilter.builder()
                .categories(reportFilter.getCategories())
                .startDate(reportFilter.getStartDate())
                .endDate(reportFilter.getEndDate())
                .vendors(reportFilter.getVendors())
                .build();
    }

    private Report buildSaveReports(String category, String filter, UUID userId, String status) {
        return Report.builder()
                .userId(userId)
                .type(category.replace("_", " "))
                .filter(filter)
                .status(status)
                .build();
    }

    private CarbonCreditCSVContent buildCSVContent(BigDecimal totalCarbonCredit, List<String> csvPerVendor, List<String> csvPerSession){
        log.info("Building CSV content..");
        return CarbonCreditCSVContent.builder()
                .totalCarbonCredit(totalCarbonCredit)
                .csvContentPerVendorData(csvPerVendor)
                .csvContentPerSessionData(csvPerSession)
                .build();
    }
}
