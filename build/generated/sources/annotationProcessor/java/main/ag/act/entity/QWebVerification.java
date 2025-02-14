package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWebVerification is a Querydsl query type for WebVerification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWebVerification extends EntityPathBase<WebVerification> {

    private static final long serialVersionUID = 1550714632L;

    public static final QWebVerification webVerification = new QWebVerification("webVerification");

    public final ComparablePath<java.util.UUID> authenticationReference = createComparable("authenticationReference", java.util.UUID.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final StringPath verificationCode = createString("verificationCode");

    public final DateTimePath<java.time.LocalDateTime> verificationCodeBaseDateTime = createDateTime("verificationCodeBaseDateTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> verificationCodeEndDateTime = createDateTime("verificationCodeEndDateTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> verificationCodeRedeemedAt = createDateTime("verificationCodeRedeemedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> verificationCodeStartDateTime = createDateTime("verificationCodeStartDateTime", java.time.LocalDateTime.class);

    public QWebVerification(String variable) {
        super(WebVerification.class, forVariable(variable));
    }

    public QWebVerification(Path<? extends WebVerification> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWebVerification(PathMetadata metadata) {
        super(WebVerification.class, metadata);
    }

}

