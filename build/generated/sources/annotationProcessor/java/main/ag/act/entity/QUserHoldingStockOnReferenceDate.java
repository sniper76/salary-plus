package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserHoldingStockOnReferenceDate is a Querydsl query type for UserHoldingStockOnReferenceDate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserHoldingStockOnReferenceDate extends EntityPathBase<UserHoldingStockOnReferenceDate> {

    private static final long serialVersionUID = -477472235L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserHoldingStockOnReferenceDate userHoldingStockOnReferenceDate = new QUserHoldingStockOnReferenceDate("userHoldingStockOnReferenceDate");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> quantity = createNumber("quantity", Long.class);

    public final DatePath<java.time.LocalDate> referenceDate = createDate("referenceDate", java.time.LocalDate.class);

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final QStock stock;

    public final StringPath stockCode = createString("stockCode");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUserHoldingStockOnReferenceDate(String variable) {
        this(UserHoldingStockOnReferenceDate.class, forVariable(variable), INITS);
    }

    public QUserHoldingStockOnReferenceDate(Path<? extends UserHoldingStockOnReferenceDate> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserHoldingStockOnReferenceDate(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserHoldingStockOnReferenceDate(PathMetadata metadata, PathInits inits) {
        this(UserHoldingStockOnReferenceDate.class, metadata, inits);
    }

    public QUserHoldingStockOnReferenceDate(Class<? extends UserHoldingStockOnReferenceDate> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.stock = inits.isInitialized("stock") ? new QStock(forProperty("stock"), inits.get("stock")) : null;
    }

}

