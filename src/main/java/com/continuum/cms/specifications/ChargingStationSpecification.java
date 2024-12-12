package com.continuum.cms.specifications;

import com.continuum.cms.entity.ChargingStation;
import com.continuum.cms.entity.CompositeId;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.text.MessageFormat;
import java.util.List;

public class ChargingStationSpecification {

    public static Specification<ChargingStation> withSearch(String search) {
        String searchString = MessageFormat.format("%{0}%", search.toLowerCase());
        return (root, query, builder) -> builder.or(
                builder.like(builder.lower(root.get("name")), searchString),
                builder.like(builder.lower(root.get("email")), searchString)
        );
    }

    public static Specification<ChargingStation> withLocation(List<String> cities) {
        return (root, query, builder) -> {
            Predicate combinedPredicate = cities.stream()
                    .map(s -> builder.equal(builder.lower(root.get("city")), s.toLowerCase()))
                    .reduce(builder::or)
                    .orElse(builder.disjunction());
            return combinedPredicate;
        };
    }

    public static Specification<ChargingStation> withVendorCode(List<String> vendorCodes) {
        return (root, query, builder) -> {
            Join<ChargingStation, CompositeId> compositeIdJoin = root.join("compositeId");
            Predicate[] predicates = vendorCodes.stream()
                    .map(vendorCode -> builder.equal(compositeIdJoin.get("vendorCode"), vendorCode))
                    .toArray(Predicate[]::new);
            return builder.or(predicates);
        };
    }
}
