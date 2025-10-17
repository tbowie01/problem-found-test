package com.problemfound.specification;

import com.problemfound.dto.ProblemFilterDTO;
import com.problemfound.model.Problem;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ProblemSpecifications {

    public static Specification<Problem> filterProblems(ProblemFilterDTO filter) {
        return (root,query,cb) -> {
            Predicate predicate = cb.conjunction();

            if (filter.getStartDate() != null && filter.getEndDate() != null) {
                predicate = cb.and(predicate,
                        cb.between(root.get("sourceCreated"),
                                filter.getStartDate().atStartOfDay(),
                                filter.getEndDate().plusDays(1).atStartOfDay()));
            } else if (filter.getStartDate() != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("sourceCreated"),
                                filter.getStartDate().atStartOfDay()));
            } else if (filter.getEndDate() != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("sourceCreated"),
                                filter.getEndDate().plusDays(1).atStartOfDay()));
            }

            if (filter.getSource() != null && !filter.getSource().isEmpty()) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("source"), filter.getSource()));
            }

            if (filter.getKeyword() != null && !filter.getKeyword().isEmpty()) {
                Join<Object, Object> keywordsJoin = root.join("keywords");
                predicate = cb.and(predicate,
                        cb.like(cb.lower(keywordsJoin.get("keyword")),
                                "%" + filter.getKeyword().toLowerCase() + "%"));
                query.distinct(true);
            }

            return predicate;
        };
    }
}
