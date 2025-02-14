package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserPushAgreement is a Querydsl query type for UserPushAgreement
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserPushAgreement extends EntityPathBase<UserPushAgreement> {

    private static final long serialVersionUID = -1918526658L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserPushAgreement userPushAgreement = new QUserPushAgreement("userPushAgreement");

    public final BooleanPath agreeToReceive = createBoolean("agreeToReceive");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final EnumPath<ag.act.enums.push.UserPushAgreementType> type = createEnum("type", ag.act.enums.push.UserPushAgreementType.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final QUser user;

    public QUserPushAgreement(String variable) {
        this(UserPushAgreement.class, forVariable(variable), INITS);
    }

    public QUserPushAgreement(Path<? extends UserPushAgreement> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserPushAgreement(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserPushAgreement(PathMetadata metadata, PathInits inits) {
        this(UserPushAgreement.class, metadata, inits);
    }

    public QUserPushAgreement(Class<? extends UserPushAgreement> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

