package com.continuum.cms.specifications;

import com.continuum.cms.entity.Vendor;
import com.continuum.cms.entity.VendorBankDetails;
import com.continuum.cms.entity.VendorModules;
import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class VendorSpecifications {
    public static Specification<Vendor> withSearch(String search) {
        String searchString = MessageFormat.format("%{0}%", search.toLowerCase());
        return (root, query, builder) -> builder.or(
                builder.like(builder.lower(root.get("name")), searchString)
        );
    }

    public static Specification<Vendor> withStatus(List<String> status) {
        return (root, query, builder) -> {
            Predicate combinedPredicate = status.stream()
                    .map(s -> builder.equal(builder.lower(root.get("status")), s.toLowerCase()))
                    .reduce(builder::or)
                    .orElse(builder.disjunction());
            return combinedPredicate;
        };
    }

    public static Specification<Vendor> withModules(List<String> modules) {
        return (root, query, builder) -> {
            Subquery<UUID> subquery = createSubqueryForModules(query, builder, modules);
            return root.get("id").in(subquery);
        };
    }

    private static Subquery<UUID> createSubqueryForModules(CriteriaQuery<?> query, CriteriaBuilder builder, List<String> modules) {
        Subquery<UUID> subquery = query.subquery(UUID.class);
        Root<VendorModules> subRoot = subquery.from(VendorModules.class);
        Predicate modulePredicate = subRoot.get("module").get("moduleName").in(modules);
        Predicate activePredicate = builder.isTrue(subRoot.get("active")); // Ensure active status is true
        subquery.select(subRoot.get("vendor").get("id"))
                .where(builder.and(modulePredicate, activePredicate));
        return subquery;
    }

    public static Specification<VendorBankDetails> filterVendorDetails(List<UUID> vendors,Date startDate, Date endDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!CollectionUtils.isEmpty(vendors)) {
                predicates.add(root.get("vendor").get("id").in(vendors));
            }

            if (ObjectUtils.isNotEmpty(startDate) && ObjectUtils.isNotEmpty(endDate)) {
                predicates.add(criteriaBuilder.between(root.get("dateCreated"), startDate, endDate));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}


