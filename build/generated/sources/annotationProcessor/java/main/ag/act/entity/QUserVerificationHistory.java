package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserVerificationHistory is a Querydsl query type for UserVerificationHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserVerificationHistory extends EntityPathBase<UserVerificationHistory> {

    private static final long serialVersionUID = 1247838887L;

    public static final QUserVerificationHistory userVerificationHistory = new QUserVerificationHistory("userVerificationHistory");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> digitalDocumentUserId = createNumber("digitalDocumentUserId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<ag.act.enums.verification.VerificationOperationType> operationType = createEnum("operationType", ag.act.enums.verification.VerificationOperationType.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final StringPath userIp = createString("userIp");

    public final EnumPath<ag.act.enums.verification.VerificationType> verificationType = createEnum("verificationType", ag.act.enums.verification.VerificationType.class);

    public QUserVerificationHistory(String variable) {
        super(UserVerificationHistory.class, forVariable(variable));
    }

    public QUserVerificationHistory(Path<? extends UserVerificationHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserVerificationHistory(PathMetadata metadata) {
        super(UserVerificationHistory.class, metadata);
    }

}

