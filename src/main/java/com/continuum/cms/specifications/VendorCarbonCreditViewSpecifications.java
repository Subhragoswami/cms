package com.continuum.cms.specifications;

import com.continuum.cms.entity.CarbonCreditDashboard;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public class VendorCarbonCreditViewSpecifications {

    public static Specification<CarbonCreditDashboard> withSearch(String vendorName) {
        return (root, query, criteriaBuilder) -> {
            if (vendorName == null || vendorName.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("vendorName")), "%" + vendorName.toLowerCase() + "%");
        };
    }

    public static Specification<CarbonCreditDashboard> withVendorIds(List<UUID> vendorIds) {
        return (root, query, builder) -> {
            Predicate combinedPredicate = vendorIds.stream()
                    .map(id -> builder.equal(root.get("vendorId"), id))
                    .reduce(builder::or)
                    .orElse(builder.disjunction());
            return combinedPredicate;
        };
    }
}
