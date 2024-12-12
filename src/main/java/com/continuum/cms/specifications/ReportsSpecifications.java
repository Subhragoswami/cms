package com.continuum.cms.specifications;

import com.continuum.cms.entity.ChargingSession;
import com.continuum.cms.entity.Report;
import com.continuum.cms.model.request.ReportFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;

import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ReportsSpecifications {
    public static Specification<Report> withSearch(String search) {
        log.info("Searching by category : {}", search);
        String searchString = MessageFormat.format("%{0}%", search.toLowerCase());
        return (root, query, builder) -> builder.or(
                builder.like(builder.lower(root.get("type")), searchString)
        );
    }
    public static Specification<Report> filterByDate(ReportFilter reportFilter) {
        log.info("Filtering by start and end date : {}, {}", reportFilter.getStartDate(), reportFilter.getEndDate());
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!CollectionUtils.isEmpty(reportFilter.getCategories())) {
                List<String> categories = reportFilter.getCategories().stream().map(String::toLowerCase).collect(Collectors.toList());
                predicates.add(root.get("type").in(categories));
            }
            if (ObjectUtils.isNotEmpty(reportFilter.getStartDate()) && ObjectUtils.isNotEmpty(reportFilter.getEndDate())) {
                Timestamp startTimestamp = Timestamp.valueOf(reportFilter.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                Timestamp endTimestamp = Timestamp.valueOf(reportFilter.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().with(LocalTime.of(23, 59, 0)));
                    predicates.add(criteriaBuilder.between(root.get("dateCreated"), startTimestamp, endTimestamp));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
