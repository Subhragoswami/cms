package com.continuum.cms.service;

import com.continuum.cms.dao.VendorUserDao;
import com.continuum.cms.entity.*;
import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.model.request.ReportFilter;
import com.continuum.cms.repository.FilesRepository;
import com.continuum.cms.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.continuum.cms.util.AppConstants.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReportStorageService {
    private final VendorUserDao vendorUserDao;
    private final FilesRepository filesRepository;

    public void csvRecordsFromVendor(List<String> csvContent, List<VendorBankDetails> vendorBankDetailsList) {
        log.info("Creating CSV records from vendor");

        List<Object[]> userCounts = vendorUserDao.getUserCountsByVendorId();
        Map<UUID, Long> userCountMap = userCounts.stream()
                .collect(Collectors.toMap(
                        row -> (UUID) row[0],
                        row -> (Long) row[1]
                ));

        vendorBankDetailsList.parallelStream()
                .map(vendorBankDetails -> formatVendorRecord(vendorBankDetails, userCountMap))
                .forEach(csvContent::add);
    }

    private String formatVendorRecord(VendorBankDetails vendorBankDetails, Map<UUID, Long> userCountMap) {
        Vendor vendor = vendorBankDetails.getVendor();

        StringBuilder builder = new StringBuilder();
        appendField(builder, vendor.getName());
        appendField(builder, vendor.getStatus());
        appendField(builder, vendor.getAddress1());
        appendField(builder, vendor.getCity());
        appendField(builder, vendor.getState());
        appendField(builder, vendor.getPincode());
        appendField(builder, vendor.getCountry());
        appendField(builder, vendorBankDetails.getBankAccountName());
        appendField(builder, vendorBankDetails.getBankAccountType());
        appendField(builder, vendorBankDetails.getCin());
        appendField(builder, vendorBankDetails.getIfscCode());
        appendField(builder, vendorBankDetails.getPan());
        appendField(builder, vendorBankDetails.getGst());
        appendField(builder, vendorBankDetails.getTin());
        appendField(builder, vendorBankDetails.getBankAccountNumber());
        appendField(builder, DateUtil.formatDateToString(vendorBankDetails.getDateCreated()));
        appendField(builder, userCountMap.getOrDefault(vendor.getId(), 0L));

        if (!builder.isEmpty()) {
            builder.setLength(builder.length() - 1);
        }

        return builder.toString();
    }

    public void csvRecordsFromVendorCarbonCredit(List<String> csvContent, List<VendorCarbonCreditEntry> totalCarbonCreditPointsByVendor) {
        log.info("Creating CSV for per vendor carbon credit points");
        totalCarbonCreditPointsByVendor.forEach(entry -> {
            String vendorName = entry.getVendorName();
            UUID vendorId = entry.getVendorId();
            BigDecimal totalPoints = entry.getTotalPoints();
            log.info("Vendor Name: {}, Vendor ID: {}, Total Carbon Credit Points: {}",
                    vendorName, vendorId, totalPoints);
            String line = String.format("%s,%s,%s",
                    vendorName,
                    vendorId,
                    totalPoints);
            csvContent.add(line);
        });
        log.info("cvs content : {}", csvContent);
    }

    public void csvRecordsFromCarbonCreditPerSession(List<String> csvContent, List<ChargingSession> totalCarbonCreditPointsByVendor) {
        log.info("Creating CSV records from carbon credit data for each session");
        List<VendorCustomer> customers = totalCarbonCreditPointsByVendor.stream()
                .map(ChargingSession::getVendorCustomer)
                .filter(Objects::nonNull)
                .toList();

        List<String> emails = customers.stream()
                .map(VendorCustomer::getEmail)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<String> mobiles = customers.stream()
                .map(VendorCustomer::getMobile)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        log.info("Processing for email and mobile decryption");
        Map<String, List<String>> decryptedEmails = EncryptionUtil.bulkDecryptParallel(emails);
        Map<String, List<String>> decryptedMobiles = EncryptionUtil.bulkDecryptParallel(mobiles);

        log.info("Processing per session details");
        totalCarbonCreditPointsByVendor.parallelStream().forEach(session -> {
            String record = processSession(session, decryptedEmails, decryptedMobiles);
            synchronized (csvContent) {
                csvContent.add(record);
            }
        });
    }

    private String processSession(ChargingSession session, Map<String, List<String>> decryptedEmailsList,  Map<String, List<String>> decryptedMobiles) {
        VendorBankDetails vendorBankDetails = session.getVendorBankDetails();
        Vendor vendor = vendorBankDetails != null ? vendorBankDetails.getVendor() : null;
        VendorCustomer customer = session.getVendorCustomer();
        ChargingStation station = session.getChargingStation();
        CarbonCreditDetail creditDetail = session.getCarbonCreditDetail();

        String decryptedEmail = customer != null && customer.getEmail() != null ? getFirstDecryptedValue(decryptedEmailsList.get(customer.getEmail())) : " ";
        String decryptedMobile = customer != null && customer.getMobile() != null ? getFirstDecryptedValue(decryptedMobiles.get(customer.getMobile())) : " ";

        StringBuilder builder = new StringBuilder();
        appendField(builder, vendor != null ? vendor.getName() : " ");
        appendField(builder, vendor != null ? vendor.getId() : " ");
        appendField(builder, station != null ? station.getName() : " ");
        appendField(builder, session.getStartAt());
        appendField(builder, session.getStopAt());
        appendField(builder, session.getUsedEnergy());
        appendField(builder, customer != null ? customer.getFirstName() + " " + customer.getLastName() : " ");
        appendField(builder, decryptedEmail);
        appendField(builder, decryptedMobile);
        appendField(builder, session.getVehicleType());
        appendField(builder, vendor != null ? vendor.getAddress1() + "," + vendor.getCity() + "," + vendor.getState() : " ");
        appendField(builder, session.getLatitude());
        appendField(builder, session.getLongitude());
        appendField(builder, session.getTransactionId() != null ? session.getTransactionId() : " ");
        appendField(builder, vendor != null ? vendor.getDateCreated() : " ");
        appendField(builder, creditDetail != null ? formatCreditScorePoints(creditDetail.getCreditScorePoints()) : " ");

        if (builder.length() > 0 && builder.charAt(builder.length() - 1) == ',') {
            builder.setLength(builder.length() - 1);
        }

        return builder.toString();
    }
    private String getFirstDecryptedValue(List<String> decryptedValues) {
        return decryptedValues != null && !decryptedValues.isEmpty() ? decryptedValues.get(0) : " ";
    }
    private void appendField(StringBuilder builder, Object field) {
        String value = field != null ? field.toString() : " ";
        builder.append("\"").append(value).append("\",");
    }

    private String formatCreditScorePoints(BigDecimal creditScorePoints) {
        return creditScorePoints != null ? creditScorePoints.setScale(10, BigDecimal.ROUND_HALF_UP).toPlainString() : " ";
    }



    public UUID downloadCSVFileForVendor(ReportFilter reportFilter,List<String> dataList, String fileName, Report report) {
        try {
            String generatedFileName = fileName + "_" + getCurrentDate() + ".csv";
            log.info("Preparing CSV file for : {}", generatedFileName);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            writeCSVForVendor(byteArrayOutputStream, reportFilter, dataList);
            byte[] csvData = byteArrayOutputStream.toByteArray();
            FileData fileData = filesRepository.save(FileData.builder()
                            .type("Super_Admin_"+fileName)
                            .contentType("CSV")
                            .data(csvData)
                            .name(generatedFileName)
                            .identifier(report.getId().toString())
                            .build());
            log.info("Vendor CSV file stored successfully.");
            return  fileData.getId();
        } catch (Exception ex) {
            log.error("An error occurred while writing the CSV file: {}", ex.getMessage());
            throw new CMSException(ErrorConstants.REPORT_ERROR_CODE, ErrorConstants.REPORT_ERROR_MESSAGE);
        }
    }
    public UUID downloadCSVFileForCarbonCredit(ReportFilter reportFilter, CarbonCreditCSVContent carbonCreditCSVContent, String fileName, Report report) {
        try {
            String generatedFileName = fileName + "_" + getCurrentDate() + ".csv";
            log.info("Preparing CSV file for : {}", generatedFileName);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            writeCSVForCarbonCredit(reportFilter , byteArrayOutputStream, carbonCreditCSVContent);
            byte[] csvData = byteArrayOutputStream.toByteArray();
            FileData fileData = filesRepository.save(FileData.builder()
                    .type("Super_Admin_"+fileName)
                    .contentType("CSV")
                    .data(csvData)
                    .name(generatedFileName)
                    .identifier(report.getId().toString())
                    .build());
            log.info("Carbon Credit CSV file stored successfully.");
            return  fileData.getId();
        } catch (Exception ex) {
            log.error("An error occurred while writing the CSV file: {}", ex.getMessage());
            return null;
        }
    }

    private static void writeCSVForVendor(OutputStream outputStream, ReportFilter reportFilter, List<String> reportData) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            log.info("Writing csv for carbon credit report");
            writer.write("Category, " + CATEGORY_VENDOR);
            writer.newLine();
            if (ObjectUtils.isNotEmpty(reportFilter.getStartDate()) && ObjectUtils.isNotEmpty(reportFilter.getEndDate())) {
                writer.write("Duration, " + DateUtil.getDateFormat(reportFilter.getStartDate()) + " to " + DateUtil.getDateFormat(reportFilter.getEndDate()));
                writer.newLine();
            }
            writer.newLine();
            writer.write(HEADER_FOR_VENDOR);
            writer.newLine();
            for (String vendorDatum : reportData) {
                writer.write(String.join("\t", vendorDatum));
                writer.newLine();
            }
        } catch (Exception e) {
            log.error("An error occurred while downloading the CSV file: {}", e.getMessage());
            throw new CMSException(ErrorConstants.REPORT_ERROR_CODE, ErrorConstants.REPORT_ERROR_MESSAGE);
        }
    }
    private static void writeCSVForCarbonCredit(ReportFilter reportFilter, OutputStream outputStream, CarbonCreditCSVContent carbonCreditCSVContent) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            log.info("Writing csv for carbon credit report");
            writer.write("Category, " + CATEGORY_CARBON_CREDIT);
            writer.newLine();
            if (ObjectUtils.isNotEmpty(reportFilter.getStartDate()) && ObjectUtils.isNotEmpty(reportFilter.getEndDate())) {
                writer.write("Duration, " + DateUtil.getDateFormat(reportFilter.getStartDate()) + " to " + DateUtil.getDateFormat(reportFilter.getEndDate()));
                writer.newLine();
            }
            writer.newLine();
            writer.write("Total Carbon Credit generated by all the vendors," + carbonCreditCSVContent.getTotalCarbonCredit());
            writer.newLine();
            writer.newLine();
            writer.write("Vendor Name, Vendor ID, Total Generated Carbon credit");
            writer.newLine();
            for (String vendorDatum : carbonCreditCSVContent.getCsvContentPerVendorData()) {
                writer.write(String.join("\t", vendorDatum));
                writer.newLine();
            }
            writer.newLine();
            writer.newLine();
            writer.write(HEADER_FOR_CARBON_CREDIT);
            writer.newLine();
            for (String vendorDatum : carbonCreditCSVContent.getCsvContentPerSessionData()) {
                writer.write(String.join("\t", vendorDatum));
                writer.newLine();
            }

        } catch (Exception e) {
            log.error("An error occurred while downloading the CSV file: {}", e.getMessage());
            throw new CMSException(ErrorConstants.REPORT_ERROR_CODE, ErrorConstants.REPORT_ERROR_MESSAGE);
        }
    }
    private static String getCurrentDate() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }
}
