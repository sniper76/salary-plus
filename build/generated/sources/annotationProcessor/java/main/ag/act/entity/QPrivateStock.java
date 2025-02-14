package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPrivateStock is a Querydsl query type for PrivateStock
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPrivateStock extends EntityPathBase<PrivateStock> {

    private static final long serialVersionUID = -398375302L;

    public static final QPrivateStock privateStock = new QPrivateStock("privateStock");

    public final NumberPath<Integer> closingPrice = createNumber("closingPrice", Integer.class);

    public final StringPath code = createString("code");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final StringPath fullName = createString("fullName");

    public final StringPath marketType = createString("marketType");

    public final StringPath name = createString("name");

    public final StringPath standardCode = createString("standardCode");

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final StringPath stockType = createString("stockType");

    public final NumberPath<Long> totalIssuedQuantity = createNumber("totalIssuedQuantity", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QPrivateStock(String variable) {
        super(PrivateStock.class, forVariable(variable));
    }

    public QPrivateStock(Path<? extends PrivateStock> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPrivateStock(PathMetadata metadata) {
        super(PrivateStock.class, metadata);
    }

}

