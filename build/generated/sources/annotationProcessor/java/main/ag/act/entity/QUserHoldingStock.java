package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserHoldingStock is a Querydsl query type for UserHoldingStock
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserHoldingStock extends EntityPathBase<UserHoldingStock> {

    private static final long serialVersionUID = 2029533477L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserHoldingStock userHoldingStock = new QUserHoldingStock("userHoldingStock");

    public final NumberPath<Long> cashQuantity = createNumber("cashQuantity", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> creditQuantity = createNumber("creditQuantity", Long.class);

    public final NumberPath<Integer> displayOrder = createNumber("displayOrder", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> purchasePrice = createNumber("purchasePrice", Long.class);

    public final NumberPath<Long> quantity = createNumber("quantity", Long.class);

    public final NumberPath<Long> secureLoanQuantity = createNumber("secureLoanQuantity", Long.class);

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final QStock stock;

    public final StringPath stockCode = createString("stockCode");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUserHoldingStock(String variable) {
        this(UserHoldingStock.class, forVariable(variable), INITS);
    }

    public QUserHoldingStock(Path<? extends UserHoldingStock> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserHoldingStock(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserHoldingStock(PathMetadata metadata, PathInits inits) {
        this(UserHoldingStock.class, metadata, inits);
    }

    public QUserHoldingStock(Class<? extends UserHoldingStock> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.stock = inits.isInitialized("stock") ? new QStock(forProperty("stock"), inits.get("stock")) : null;
    }

}

