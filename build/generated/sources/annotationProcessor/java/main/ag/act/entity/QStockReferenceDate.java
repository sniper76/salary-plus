package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStockReferenceDate is a Querydsl query type for StockReferenceDate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStockReferenceDate extends EntityPathBase<StockReferenceDate> {

    private static final long serialVersionUID = 1718166538L;

    public static final QStockReferenceDate stockReferenceDate = new QStockReferenceDate("stockReferenceDate");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DatePath<java.time.LocalDate> referenceDate = createDate("referenceDate", java.time.LocalDate.class);

    public final StringPath stockCode = createString("stockCode");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QStockReferenceDate(String variable) {
        super(StockReferenceDate.class, forVariable(variable));
    }

    public QStockReferenceDate(Path<? extends StockReferenceDate> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStockReferenceDate(PathMetadata metadata) {
        super(StockReferenceDate.class, metadata);
    }

}

