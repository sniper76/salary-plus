package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserBadgeVisibility is a Querydsl query type for UserBadgeVisibility
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserBadgeVisibility extends EntityPathBase<UserBadgeVisibility> {

    private static final long serialVersionUID = 439702211L;

    public static final QUserBadgeVisibility userBadgeVisibility = new QUserBadgeVisibility("userBadgeVisibility");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isVisible = createBoolean("isVisible");

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final EnumPath<ag.act.enums.UserBadgeType> type = createEnum("type", ag.act.enums.UserBadgeType.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUserBadgeVisibility(String variable) {
        super(UserBadgeVisibility.class, forVariable(variable));
    }

    public QUserBadgeVisibility(Path<? extends UserBadgeVisibility> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserBadgeVisibility(PathMetadata metadata) {
        super(UserBadgeVisibility.class, metadata);
    }

}

