package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLinkUrl is a Querydsl query type for LinkUrl
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLinkUrl extends EntityPathBase<LinkUrl> {

    private static final long serialVersionUID = -98378866L;

    public static final QLinkUrl linkUrl1 = new QLinkUrl("linkUrl1");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath linkTitle = createString("linkTitle");

    public final EnumPath<ag.act.enums.LinkType> linkType = createEnum("linkType", ag.act.enums.LinkType.class);

    public final StringPath linkUrl = createString("linkUrl");

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QLinkUrl(String variable) {
        super(LinkUrl.class, forVariable(variable));
    }

    public QLinkUrl(Path<? extends LinkUrl> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLinkUrl(PathMetadata metadata) {
        super(LinkUrl.class, metadata);
    }

}

