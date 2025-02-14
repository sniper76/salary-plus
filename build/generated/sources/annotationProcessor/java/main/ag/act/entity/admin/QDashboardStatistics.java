package ag.act.entity.admin;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDashboardStatistics is a Querydsl query type for DashboardStatistics
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDashboardStatistics extends EntityPathBase<DashboardStatistics> {

    private static final long serialVersionUID = -1995461135L;

    public static final QDashboardStatistics dashboardStatistics = new QDashboardStatistics("dashboardStatistics");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath date = createString("date");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<ag.act.enums.admin.DashboardStatisticsType> type = createEnum("type", ag.act.enums.admin.DashboardStatisticsType.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Double> value = createNumber("value", Double.class);

    public QDashboardStatistics(String variable) {
        super(DashboardStatistics.class, forVariable(variable));
    }

    public QDashboardStatistics(Path<? extends DashboardStatistics> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDashboardStatistics(PathMetadata metadata) {
        super(DashboardStatistics.class, metadata);
    }

}

