package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSolidarity is a Querydsl query type for Solidarity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSolidarity extends EntityPathBase<Solidarity> {

    private static final long serialVersionUID = -1524160519L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSolidarity solidarity = new QSolidarity("solidarity");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final BooleanPath hasEverHadLeader = createBoolean("hasEverHadLeader");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QSolidarityDailySummary mostRecentDailySummary;

    public final QSolidarityDailySummary secondMostRecentDailySummary;

    public final QSolidarityLeader solidarityLeader;

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final QStock stock;

    public final StringPath stockCode = createString("stockCode");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QSolidarity(String variable) {
        this(Solidarity.class, forVariable(variable), INITS);
    }

    public QSolidarity(Path<? extends Solidarity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSolidarity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSolidarity(PathMetadata metadata, PathInits inits) {
        this(Solidarity.class, metadata, inits);
    }

    public QSolidarity(Class<? extends Solidarity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.mostRecentDailySummary = inits.isInitialized("mostRecentDailySummary") ? new QSolidarityDailySummary(forProperty("mostRecentDailySummary")) : null;
        this.secondMostRecentDailySummary = inits.isInitialized("secondMostRecentDailySummary") ? new QSolidarityDailySummary(forProperty("secondMostRecentDailySummary")) : null;
        this.solidarityLeader = inits.isInitialized("solidarityLeader") ? new QSolidarityLeader(forProperty("solidarityLeader"), inits.get("solidarityLeader")) : null;
        this.stock = inits.isInitialized("stock") ? new QStock(forProperty("stock"), inits.get("stock")) : null;
    }

}

