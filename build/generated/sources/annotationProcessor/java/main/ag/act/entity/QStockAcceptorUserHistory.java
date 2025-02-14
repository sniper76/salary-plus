package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStockAcceptorUserHistory is a Querydsl query type for StockAcceptorUserHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStockAcceptorUserHistory extends EntityPathBase<StockAcceptorUserHistory> {

    private static final long serialVersionUID = 870408719L;

    public static final QStockAcceptorUserHistory stockAcceptorUserHistory = new QStockAcceptorUserHistory("stockAcceptorUserHistory");

    public final DateTimePath<java.time.LocalDateTime> birthDate = createDateTime("birthDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> firstNumberOfIdentification = createNumber("firstNumberOfIdentification", Integer.class);

    public final StringPath hashedPhoneNumber = createString("hashedPhoneNumber");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final StringPath stockCode = createString("stockCode");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QStockAcceptorUserHistory(String variable) {
        super(StockAcceptorUserHistory.class, forVariable(variable));
    }

    public QStockAcceptorUserHistory(Path<? extends StockAcceptorUserHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStockAcceptorUserHistory(PathMetadata metadata) {
        super(StockAcceptorUserHistory.class, metadata);
    }

}

