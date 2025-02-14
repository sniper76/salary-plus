package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSolidarityDailySummary is a Querydsl query type for SolidarityDailySummary
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSolidarityDailySummary extends EntityPathBase<SolidarityDailySummary> {

    private static final long serialVersionUID = 1490637286L;

    public static final QSolidarityDailySummary solidarityDailySummary = new QSolidarityDailySummary("solidarityDailySummary");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> marketValue = createNumber("marketValue", Long.class);

    public final NumberPath<Integer> memberCount = createNumber("memberCount", Integer.class);

    public final NumberPath<Double> stake = createNumber("stake", Double.class);

    public final NumberPath<Long> stockQuantity = createNumber("stockQuantity", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QSolidarityDailySummary(String variable) {
        super(SolidarityDailySummary.class, forVariable(variable));
    }

    public QSolidarityDailySummary(Path<? extends SolidarityDailySummary> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSolidarityDailySummary(PathMetadata metadata) {
        super(SolidarityDailySummary.class, metadata);
    }

}

