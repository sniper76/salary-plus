package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDigitalProxy is a Querydsl query type for DigitalProxy
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDigitalProxy extends EntityPathBase<DigitalProxy> {

    private static final long serialVersionUID = -1271044515L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDigitalProxy digitalProxy = new QDigitalProxy("digitalProxy");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final ListPath<DigitalProxyApproval, QDigitalProxyApproval> digitalProxyApprovalList = this.<DigitalProxyApproval, QDigitalProxyApproval>createList("digitalProxyApprovalList", DigitalProxyApproval.class, QDigitalProxyApproval.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPost post;

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final DateTimePath<java.time.LocalDateTime> targetEndDate = createDateTime("targetEndDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> targetStartDate = createDateTime("targetStartDate", java.time.LocalDateTime.class);

    public final StringPath templateId = createString("templateId");

    public final StringPath templateName = createString("templateName");

    public final StringPath templateRole = createString("templateRole");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QDigitalProxy(String variable) {
        this(DigitalProxy.class, forVariable(variable), INITS);
    }

    public QDigitalProxy(Path<? extends DigitalProxy> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDigitalProxy(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDigitalProxy(PathMetadata metadata, PathInits inits) {
        this(DigitalProxy.class, metadata, inits);
    }

    public QDigitalProxy(Class<? extends DigitalProxy> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
    }

}

