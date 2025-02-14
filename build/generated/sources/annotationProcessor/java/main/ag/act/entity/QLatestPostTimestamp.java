package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLatestPostTimestamp is a Querydsl query type for LatestPostTimestamp
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLatestPostTimestamp extends EntityPathBase<LatestPostTimestamp> {

    private static final long serialVersionUID = 1245954824L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLatestPostTimestamp latestPostTimestamp = new QLatestPostTimestamp("latestPostTimestamp");

    public final EnumPath<ag.act.enums.BoardCategory> boardCategory = createEnum("boardCategory", ag.act.enums.BoardCategory.class);

    public final EnumPath<ag.act.enums.BoardGroup> boardGroup = createEnum("boardGroup", ag.act.enums.BoardGroup.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QStock stock;

    public final DateTimePath<java.time.LocalDateTime> timestamp = createDateTime("timestamp", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QLatestPostTimestamp(String variable) {
        this(LatestPostTimestamp.class, forVariable(variable), INITS);
    }

    public QLatestPostTimestamp(Path<? extends LatestPostTimestamp> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLatestPostTimestamp(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLatestPostTimestamp(PathMetadata metadata, PathInits inits) {
        this(LatestPostTimestamp.class, metadata, inits);
    }

    public QLatestPostTimestamp(Class<? extends LatestPostTimestamp> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.stock = inits.isInitialized("stock") ? new QStock(forProperty("stock"), inits.get("stock")) : null;
    }

}

