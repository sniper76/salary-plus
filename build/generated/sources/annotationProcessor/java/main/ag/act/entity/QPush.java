package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPush is a Querydsl query type for Push
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPush extends EntityPathBase<Push> {

    private static final long serialVersionUID = -1683345087L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPush push = new QPush("push");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<ag.act.enums.AppLinkType> linkType = createEnum("linkType", ag.act.enums.AppLinkType.class);

    public final StringPath linkUrl = createString("linkUrl");

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final EnumPath<ag.act.enums.push.PushTargetType> pushTargetType = createEnum("pushTargetType", ag.act.enums.push.PushTargetType.class);

    public final StringPath result = createString("result");

    public final EnumPath<ag.act.enums.push.PushSendStatus> sendStatus = createEnum("sendStatus", ag.act.enums.push.PushSendStatus.class);

    public final EnumPath<ag.act.enums.push.PushSendType> sendType = createEnum("sendType", ag.act.enums.push.PushSendType.class);

    public final DateTimePath<java.time.LocalDateTime> sentEndDatetime = createDateTime("sentEndDatetime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> sentStartDatetime = createDateTime("sentStartDatetime", java.time.LocalDateTime.class);

    public final QStock stock;

    public final StringPath stockCode = createString("stockCode");

    public final QStockGroup stockGroup;

    public final NumberPath<Long> stockGroupId = createNumber("stockGroupId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> targetDatetime = createDateTime("targetDatetime", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public final StringPath topic = createString("topic");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QPush(String variable) {
        this(Push.class, forVariable(variable), INITS);
    }

    public QPush(Path<? extends Push> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPush(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPush(PathMetadata metadata, PathInits inits) {
        this(Push.class, metadata, inits);
    }

    public QPush(Class<? extends Push> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.stock = inits.isInitialized("stock") ? new QStock(forProperty("stock"), inits.get("stock")) : null;
        this.stockGroup = inits.isInitialized("stockGroup") ? new QStockGroup(forProperty("stockGroup")) : null;
    }

}

