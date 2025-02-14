package ag.act.entity.admin;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDashboardAgeStatistics is a Querydsl query type for DashboardAgeStatistics
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDashboardAgeStatistics extends EntityPathBase<DashboardAgeStatistics> {

    private static final long serialVersionUID = -1233044492L;

    public static final QDashboardAgeStatistics dashboardAgeStatistics = new QDashboardAgeStatistics("dashboardAgeStatistics");

    public final NumberPath<Long> age10Value = createNumber("age10Value", Long.class);

    public final NumberPath<Long> age20Value = createNumber("age20Value", Long.class);

    public final NumberPath<Long> age30Value = createNumber("age30Value", Long.class);

    public final NumberPath<Long> age40Value = createNumber("age40Value", Long.class);

    public final NumberPath<Long> age50Value = createNumber("age50Value", Long.class);

    public final NumberPath<Long> age60Value = createNumber("age60Value", Long.class);

    public final NumberPath<Long> age70Value = createNumber("age70Value", Long.class);

    public final NumberPath<Long> age80Value = createNumber("age80Value", Long.class);

    public final NumberPath<Long> age90Value = createNumber("age90Value", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath date = createString("date");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<ag.act.enums.admin.DashboardStatisticsType> type = createEnum("type", ag.act.enums.admin.DashboardStatisticsType.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QDashboardAgeStatistics(String variable) {
        super(DashboardAgeStatistics.class, forVariable(variable));
    }

    public QDashboardAgeStatistics(Path<? extends DashboardAgeStatistics> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDashboardAgeStatistics(PathMetadata metadata) {
        super(DashboardAgeStatistics.class, metadata);
    }

}

