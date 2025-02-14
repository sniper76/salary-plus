package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSolidarityDailyStatistics is a Querydsl query type for SolidarityDailyStatistics
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSolidarityDailyStatistics extends EntityPathBase<SolidarityDailyStatistics> {

    private static final long serialVersionUID = 2129583843L;

    public static final QSolidarityDailyStatistics solidarityDailyStatistics = new QSolidarityDailyStatistics("solidarityDailyStatistics");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> marketValue = createNumber("marketValue", Long.class);

    public final NumberPath<Integer> memberCount = createNumber("memberCount", Integer.class);

    public final NumberPath<Long> solidarityId = createNumber("solidarityId", Long.class);

    public final NumberPath<Double> stake = createNumber("stake", Double.class);

    public final StringPath stockCode = createString("stockCode");

    public final NumberPath<Long> stockQuantity = createNumber("stockQuantity", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QSolidarityDailyStatistics(String variable) {
        super(SolidarityDailyStatistics.class, forVariable(variable));
    }

    public QSolidarityDailyStatistics(Path<? extends SolidarityDailyStatistics> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSolidarityDailyStatistics(PathMetadata metadata) {
        super(SolidarityDailyStatistics.class, metadata);
    }

}

