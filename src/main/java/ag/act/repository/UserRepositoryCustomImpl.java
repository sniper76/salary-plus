package ag.act.repository;

import ag.act.entity.QRole;
import ag.act.entity.QSolidarity;
import ag.act.entity.QSolidarityLeader;
import ag.act.entity.QUser;
import ag.act.entity.QUserRole;
import ag.act.entity.User;
import ag.act.enums.admin.UserFilterType;
import ag.act.enums.admin.UserSearchType;
import ag.act.model.Status;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("rawtypes")
@RequiredArgsConstructor
@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {


    private final JPAQueryFactory query;
    private final QUser user = QUser.user;
    private final QUserRole userRole = QUserRole.userRole;
    private final QRole role = QRole.role;
    private final QSolidarityLeader solidarityLeader = QSolidarityLeader.solidarityLeader;
    private final QSolidarity solidarity = QSolidarity.solidarity;
    private final Map<UserSearchType, Function<String, BooleanExpression>> userSearchTypeFunctionMap = Map.of(
        UserSearchType.NAME, user.name::contains,
        UserSearchType.USER_ID, keyword -> user.id.eq(Long.valueOf(keyword)),
        UserSearchType.NICKNAME, user.nickname::contains,
        UserSearchType.EMAIL, user.email::contains,
        UserSearchType.PHONE_NUMBER, user.hashedPhoneNumber::eq
    );

    @Override
    public Page<User> findAllByConditions(
        List<Long> exclusiveUserIds,
        List<Status> exclusiveStatuses,
        UserFilterType userFilterType,
        UserSearchType userSearchType,
        String keyword,
        Pageable pageable
    ) {
        JPAQuery<?> jpaQuery = query.from(user);
        joinQueryForUserFiltering(jpaQuery, userFilterType);
        applyConditionsToQuery(jpaQuery, exclusiveUserIds, exclusiveStatuses, userSearchType, keyword);

        Long total = jpaQuery.select(user.count()).fetchFirst();

        List<User> contents = jpaQuery
            .select(user)
            .orderBy(getOrderSpecifier(pageable))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();


        return new PageImpl<>(contents, pageable, total);
    }

    private <T> JPAQuery<T> applyConditionsToQuery(
        JPAQuery<T> query,
        List<Long> exclusiveUserIds,
        List<Status> exclusiveStatuses,
        UserSearchType userSearchType,
        String keyword
    ) {
        return query
            .where(
                user.id.notIn(exclusiveUserIds),
                user.status.notIn(exclusiveStatuses),
                searchKeywordEq(userSearchType, keyword)
            );
    }

    private <T> void joinQueryForUserFiltering(JPAQuery<T> jpaQuery, UserFilterType filterType) {
        applyAdminOrAcceptorFilterIfApplicable(jpaQuery, filterType);
        applySolidarityLeaderFilterIfApplicable(jpaQuery, filterType);
    }

    private <T> void applyAdminOrAcceptorFilterIfApplicable(JPAQuery<T> jpaQuery, UserFilterType filterType) {
        filterType.getRoleType()
            .ifPresent(roleType -> jpaQuery.join(userRole)
                .on(user.id.eq(userRole.userId))
                .join(role)
                .on(role.id.eq(userRole.roleId)
                    .and(role.type.eq(roleType)))
            );
    }

    private <T> void applySolidarityLeaderFilterIfApplicable(JPAQuery<T> jpaQuery, UserFilterType filterType) {
        if (!isFilterTypeSolidarityLeader(filterType)) {
            return;
        }

        jpaQuery.join(solidarityLeader)
            .on(user.id.eq(solidarityLeader.userId))
            .join(solidarity)
            .on(solidarity.id.eq(solidarityLeader.solidarity.id)
                .and(solidarity.status.eq(Status.ACTIVE)));
    }

    private boolean isFilterTypeSolidarityLeader(UserFilterType filterType) {
        return UserFilterType.SOLIDARITY_LEADER == filterType;
    }

    @SuppressWarnings("unchecked")
    private OrderSpecifier<?> getOrderSpecifier(Pageable pageable) {
        PathBuilder pathBuilder = new PathBuilder(User.class, "user");

        return (OrderSpecifier<?>) pageable.getSort()
            .stream()
            .findFirst()
            .map(order -> {
                final Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                final String property = order.getProperty();
                return new OrderSpecifier<>(direction, pathBuilder.get(property));
            })
            .orElse(new OrderSpecifier<>(Order.DESC, pathBuilder.get("createdAt")));
    }

    @Nullable
    private BooleanExpression searchKeywordEq(UserSearchType searchType, String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return null;
        }

        return userSearchTypeFunctionMap
            .getOrDefault(searchType, it -> null)
            .apply(keyword.trim());
    }
}