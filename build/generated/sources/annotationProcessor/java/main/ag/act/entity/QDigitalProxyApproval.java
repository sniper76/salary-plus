package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDigitalProxyApproval is a Querydsl query type for DigitalProxyApproval
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDigitalProxyApproval extends EntityPathBase<DigitalProxyApproval> {

    private static final long serialVersionUID = -1831631936L;

    public static final QDigitalProxyApproval digitalProxyApproval = new QDigitalProxyApproval("digitalProxyApproval");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final StringPath documentId = createString("documentId");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath participantId = createString("participantId");

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QDigitalProxyApproval(String variable) {
        super(DigitalProxyApproval.class, forVariable(variable));
    }

    public QDigitalProxyApproval(Path<? extends DigitalProxyApproval> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDigitalProxyApproval(PathMetadata metadata) {
        super(DigitalProxyApproval.class, metadata);
    }

}

