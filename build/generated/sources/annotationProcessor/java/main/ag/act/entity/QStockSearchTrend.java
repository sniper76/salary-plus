package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStockSearchTrend is a Querydsl query type for StockSearchTrend
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStockSearchTrend extends EntityPathBase<StockSearchTrend> {

    private static final long serialVersionUID = -1528104058L;

    public static final QStockSearchTrend stockSearchTrend = new QStockSearchTrend("stockSearchTrend");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath stockCode = createString("stockCode");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QStockSearchTrend(String variable) {
        super(StockSearchTrend.class, forVariable(variable));
    }

    public QStockSearchTrend(Path<? extends StockSearchTrend> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStockSearchTrend(PathMetadata metadata) {
        super(StockSearchTrend.class, metadata);
    }

}

