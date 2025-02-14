package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStockGroupMapping is a Querydsl query type for StockGroupMapping
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStockGroupMapping extends EntityPathBase<StockGroupMapping> {

    private static final long serialVersionUID = 214830270L;

    public static final QStockGroupMapping stockGroupMapping = new QStockGroupMapping("stockGroupMapping");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath stockCode = createString("stockCode");

    public final NumberPath<Long> stockGroupId = createNumber("stockGroupId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QStockGroupMapping(String variable) {
        super(StockGroupMapping.class, forVariable(variable));
    }

    public QStockGroupMapping(Path<? extends StockGroupMapping> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStockGroupMapping(PathMetadata metadata) {
        super(StockGroupMapping.class, metadata);
    }

}

