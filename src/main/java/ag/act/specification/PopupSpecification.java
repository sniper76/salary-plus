package ag.act.specification;


import ag.act.entity.Popup;
import ag.act.model.Status;
import ag.act.util.QueryUtil;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class PopupSpecification {
    public static Specification<Popup> isActive() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("status"), Status.ACTIVE);
    }

    public static Specification<Popup> empty() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.isTrue(criteriaBuilder.literal(true));
    }

    public static Specification<Popup> isInProgress() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
            criteriaBuilder.lessThanOrEqualTo(root.get("targetStartDatetime"), LocalDateTime.now()),
            criteriaBuilder.greaterThanOrEqualTo(root.get("targetEndDatetime"), LocalDateTime.now())
        );
    }

    public static Specification<Popup> isPast() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.lessThan(root.get("targetEndDatetime"), LocalDateTime.now());
    }

    public static Specification<Popup> isFuture() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.greaterThan(root.get("targetStartDatetime"), LocalDateTime.now());
    }

    public static Specification<Popup> titleContains(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isEmpty()) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // always true = no filtering
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), QueryUtil.toLikeString(keyword.toLowerCase()));
        };
    }
}
