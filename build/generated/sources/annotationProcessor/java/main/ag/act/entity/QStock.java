package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStock is a Querydsl query type for Stock
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStock extends EntityPathBase<Stock> {

    private static final long serialVersionUID = -641353265L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStock stock = new QStock("stock");

    public final StringPath address = createString("address");

    public final NumberPath<Integer> closingPrice = createNumber("closingPrice", Integer.class);

    public final StringPath code = createString("code");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final StringPath fullName = createString("fullName");

    public final BooleanPath isPrivate = createBoolean("isPrivate");

    public final StringPath marketType = createString("marketType");

    public final StringPath name = createString("name");

    public final StringPath representativePhoneNumber = createString("representativePhoneNumber");

    public final QSolidarity solidarity;

    public final NumberPath<Long> solidarityId = createNumber("solidarityId", Long.class);

    public final StringPath standardCode = createString("standardCode");

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final StringPath stockType = createString("stockType");

    public final NumberPath<Long> totalIssuedQuantity = createNumber("totalIssuedQuantity", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QStock(String variable) {
        this(Stock.class, forVariable(variable), INITS);
    }

    public QStock(Path<? extends Stock> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStock(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStock(PathMetadata metadata, PathInits inits) {
        this(Stock.class, metadata, inits);
    }

    public QStock(Class<? extends Stock> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.solidarity = inits.isInitialized("solidarity") ? new QSolidarity(forProperty("solidarity"), inits.get("solidarity")) : null;
    }

}

