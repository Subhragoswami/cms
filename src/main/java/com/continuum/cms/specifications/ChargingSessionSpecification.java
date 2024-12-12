package com.continuum.cms.specifications;

import com.continuum.cms.entity.ChargingSession;
import com.continuum.cms.entity.ChargingStation;
import com.continuum.cms.entity.VendorCustomer;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
public class ChargingSessionSpecification {
    public static class FilterSpecification implements Specification<ChargingSession> {
        private final List<Long> stationIds;
        private final List<String> cities;
        private final List<String> vendorCodes;
        private final Date startDate;
        private final Date endDate;




        public FilterSpecification(List<String> cities, Date startDate , Date endDate, List<Long> stationIds , List<String> vendorCodes) {
            this.stationIds = stationIds;
            this.vendorCodes = vendorCodes;
            this.cities = cities;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        @Override
        public Predicate toPredicate(Root<ChargingSession> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            log.info("Query filter for stationId: {}, cities: {}, startDate: {}, endDate: {}, vendor: {}", stationIds, cities, startDate, endDate, vendorCodes);

            List<Predicate> predicates = new ArrayList<>();
            if(!CollectionUtils.isEmpty(stationIds)){
                predicates.add(root.get("chargingStation").get("locationId").in(stationIds));
            }

            if (!CollectionUtils.isEmpty(cities)) {
                predicates.add(root.get("chargingStation").get("city").in(cities));
            }
            if(!CollectionUtils.isEmpty(vendorCodes)){
                predicates.add(root.get("compositeId").get("vendorCode").in(vendorCodes));
            }
            if (ObjectUtils.isNotEmpty(startDate) && ObjectUtils.isNotEmpty(endDate)) {
                predicates.add(criteriaBuilder.between(root.get("startAt"), startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                        endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().with(LocalTime.of(23, 59, 0))));
            }
            log.info("Prepared filtered specifications : {}", predicates);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }
    }
    public static class SearchSpecification implements Specification<ChargingSession> {
        private final String search;

        public SearchSpecification(String search) {
            this.search = search;
        }

        @Override
        public Predicate toPredicate(Root<ChargingSession> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            log.info("Query for search : {}", search);
            String searchString = "%" + search.toLowerCase() + "%";

            Join<ChargingSession, ChargingStation> stationJoin = root.join("chargingStation", JoinType.LEFT);
            Predicate cityInStationPredicate = criteriaBuilder.like(criteriaBuilder.lower(stationJoin.get("city")), searchString);

            Join<ChargingSession, VendorCustomer> customerJoin = root.join("vendorCustomer", JoinType.LEFT);
            Predicate firstNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get("firstName")), searchString);
            Predicate lastPredicate = criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get("lastName")), searchString);
            log.info("Prepared search specifications : {}", criteriaBuilder);
            return criteriaBuilder.or(cityInStationPredicate, firstNamePredicate, lastPredicate);
        }
    }
}
