package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSolidarityLeader is a Querydsl query type for SolidarityLeader
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSolidarityLeader extends EntityPathBase<SolidarityLeader> {

    private static final long serialVersionUID = -95765918L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSolidarityLeader solidarityLeader = new QSolidarityLeader("solidarityLeader");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath message = createString("message");

    public final QSolidarity solidarity;

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QSolidarityLeader(String variable) {
        this(SolidarityLeader.class, forVariable(variable), INITS);
    }

    public QSolidarityLeader(Path<? extends SolidarityLeader> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSolidarityLeader(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSolidarityLeader(PathMetadata metadata, PathInits inits) {
        this(SolidarityLeader.class, metadata, inits);
    }

    public QSolidarityLeader(Class<? extends SolidarityLeader> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.solidarity = inits.isInitialized("solidarity") ? new QSolidarity(forProperty("solidarity"), inits.get("solidarity")) : null;
    }

}

