package ag.act.entity.admin;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDashboardStockStatistics is a Querydsl query type for DashboardStockStatistics
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDashboardStockStatistics extends EntityPathBase<DashboardStockStatistics> {

    private static final long serialVersionUID = -56036149L;

    public static final QDashboardStockStatistics dashboardStockStatistics = new QDashboardStockStatistics("dashboardStockStatistics");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath date = createString("date");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath stockCode = createString("stockCode");

    public final EnumPath<ag.act.enums.admin.DashboardStatisticsType> type = createEnum("type", ag.act.enums.admin.DashboardStatisticsType.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Double> value = createNumber("value", Double.class);

    public QDashboardStockStatistics(String variable) {
        super(DashboardStockStatistics.class, forVariable(variable));
    }

    public QDashboardStockStatistics(Path<? extends DashboardStockStatistics> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDashboardStockStatistics(PathMetadata metadata) {
        super(DashboardStockStatistics.class, metadata);
    }

}

