package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStockAcceptorUser is a Querydsl query type for StockAcceptorUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStockAcceptorUser extends EntityPathBase<StockAcceptorUser> {

    private static final long serialVersionUID = -1802739003L;

    public static final QStockAcceptorUser stockAcceptorUser = new QStockAcceptorUser("stockAcceptorUser");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath stockCode = createString("stockCode");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QStockAcceptorUser(String variable) {
        super(StockAcceptorUser.class, forVariable(variable));
    }

    public QStockAcceptorUser(Path<? extends StockAcceptorUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStockAcceptorUser(PathMetadata metadata) {
        super(StockAcceptorUser.class, metadata);
    }

}

