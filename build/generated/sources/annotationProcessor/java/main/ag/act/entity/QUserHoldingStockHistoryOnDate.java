package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserHoldingStockHistoryOnDate is a Querydsl query type for UserHoldingStockHistoryOnDate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserHoldingStockHistoryOnDate extends EntityPathBase<UserHoldingStockHistoryOnDate> {

    private static final long serialVersionUID = -881445188L;

    public static final QUserHoldingStockHistoryOnDate userHoldingStockHistoryOnDate = new QUserHoldingStockHistoryOnDate("userHoldingStockHistoryOnDate");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> quantity = createNumber("quantity", Long.class);

    public final StringPath stockCode = createString("stockCode");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUserHoldingStockHistoryOnDate(String variable) {
        super(UserHoldingStockHistoryOnDate.class, forVariable(variable));
    }

    public QUserHoldingStockHistoryOnDate(Path<? extends UserHoldingStockHistoryOnDate> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserHoldingStockHistoryOnDate(PathMetadata metadata) {
        super(UserHoldingStockHistoryOnDate.class, metadata);
    }

}

