package ag.act.entity.admin;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStockRanking is a Querydsl query type for StockRanking
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStockRanking extends EntityPathBase<StockRanking> {

    private static final long serialVersionUID = -1491963290L;

    public static final QStockRanking stockRanking = new QStockRanking("stockRanking");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> marketValue = createNumber("marketValue", Long.class);

    public final NumberPath<Integer> marketValueRank = createNumber("marketValueRank", Integer.class);

    public final NumberPath<Integer> marketValueRankDelta = createNumber("marketValueRankDelta", Integer.class);

    public final NumberPath<Double> stake = createNumber("stake", Double.class);

    public final NumberPath<Integer> stakeRank = createNumber("stakeRank", Integer.class);

    public final NumberPath<Integer> stakeRankDelta = createNumber("stakeRankDelta", Integer.class);

    public final StringPath stockCode = createString("stockCode");

    public final StringPath stockName = createString("stockName");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QStockRanking(String variable) {
        super(StockRanking.class, forVariable(variable));
    }

    public QStockRanking(Path<? extends StockRanking> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStockRanking(PathMetadata metadata) {
        super(StockRanking.class, metadata);
    }

}

