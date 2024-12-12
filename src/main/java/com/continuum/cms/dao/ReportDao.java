package com.continuum.cms.dao;

import com.continuum.cms.entity.ChargingSession;
import com.continuum.cms.entity.Report;
import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.model.request.ReportFilter;
import com.continuum.cms.repository.ChargingSessionRepository;
import com.continuum.cms.repository.ReportsRepository;
import com.continuum.cms.specifications.ReportsSpecifications;
import com.continuum.cms.util.ErrorConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ReportDao {
    private final ReportsRepository reportsRepository;
    private final ChargingSessionRepository chargingSessionRepository;

    public Report saveReports(Report report) {
        log.info("Requesting to save reports in db");
        return  reportsRepository.save(report);
    }

    public void updateReportStatusById(UUID reportId, String status, UUID fileId){
        log.info("updating Report status by id : {}, {}, {}", reportId, status, fileId);
        reportsRepository.updateReportStatusById(reportId, status, fileId);
    }

    @Transactional
    public List<ChargingSession> getChargingSessionDetails(ReportFilter reportFilter, List<String> vendorCodes){
        if(CollectionUtils.isEmpty(vendorCodes)
        && ObjectUtils.isEmpty(reportFilter.getStartDate()) && ObjectUtils.isEmpty(reportFilter.getEndDate())){
            return chargingSessionRepository.findAllWithRelations();
        } else if (CollectionUtils.isEmpty(reportFilter.getVendors())
                && ObjectUtils.isNotEmpty(reportFilter.getStartDate()) && ObjectUtils.isNotEmpty(reportFilter.getEndDate())) {
            return chargingSessionRepository.findAllWithRelationsBetween(convertToLocalDateTime(reportFilter.getStartDate()), convertToEndOfDay(reportFilter.getEndDate()));
        }else if(!CollectionUtils.isEmpty(vendorCodes)
                && ObjectUtils.isEmpty(reportFilter.getStartDate()) && ObjectUtils.isEmpty(reportFilter.getEndDate())){
            return chargingSessionRepository.findAllWithRelationsByVendorCodes(vendorCodes);
        } else if (!CollectionUtils.isEmpty(vendorCodes)
                && ObjectUtils.isNotEmpty(reportFilter.getStartDate()) && ObjectUtils.isNotEmpty(reportFilter.getEndDate())) {
            return chargingSessionRepository.findAllWithRelationsBetweenAndVendorCode(convertToLocalDateTime(reportFilter.getStartDate()), convertToEndOfDay(reportFilter.getEndDate()), vendorCodes);
        }
        throw new CMSException(ErrorConstants.NOT_FOUND_ERROR_CODE, MessageFormat.format(ErrorConstants.NOT_FOUND_ERROR_MESSAGE, "Charging Session Data"));
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    private LocalDateTime convertToEndOfDay(Date date) {
        LocalDateTime localDateTime = convertToLocalDateTime(date);
        return localDateTime.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
    }

    public Page<Report> getReportList(ReportFilter reportFilter, String search, Pageable pageable) {
        log.info("Fetching Report List for the criteria : {}, {}, {}", reportFilter, search, pageable);
        if (CollectionUtils.isEmpty(reportFilter.getCategories()) && ObjectUtils.isEmpty(reportFilter.getStartDate()) && ObjectUtils
                .isEmpty(reportFilter.getEndDate()) && StringUtils.isEmpty(search)) {
            log.info("Fetching all the data from the DB: {}", pageable);
            return reportsRepository.findAll(pageable);
        }
        if (CollectionUtils.isEmpty(reportFilter.getCategories()) && ObjectUtils.isEmpty(reportFilter.getStartDate()) && ObjectUtils
                .isEmpty(reportFilter.getEndDate()) && StringUtils.isNotEmpty(search)) {
            log.info("fetching users based on the search criteria:{}", search);
            Specification<Report> searchSpec = ReportsSpecifications.withSearch(search);
            return reportsRepository.findAll(searchSpec, pageable);
        }
        Specification<Report> specification = buildSpecification(reportFilter, search);
        log.info("fetching users based on the filter criteria:{}, {}", reportFilter.getStartDate(), reportFilter.getEndDate());
        return reportsRepository.findAll(specification, pageable);
    }

    public Specification<Report> buildSpecification(ReportFilter reportFilter, String search) {
        log.info("Building specification for the Criteria : {}, {}", reportFilter, search);
        Specification<Report> specification = Specification.where(null);
        if (StringUtils.isNotEmpty(search)) {
            specification = specification.and(ReportsSpecifications.withSearch(search));
        }
        if (ObjectUtils.isNotEmpty(reportFilter)) {
            specification = specification.and(ReportsSpecifications.filterByDate(reportFilter));
        }
        return specification;
    }
}
