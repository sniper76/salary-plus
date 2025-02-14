package ag.act.entity.admin;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDashboardGenderStatistics is a Querydsl query type for DashboardGenderStatistics
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDashboardGenderStatistics extends EntityPathBase<DashboardGenderStatistics> {

    private static final long serialVersionUID = -1432195822L;

    public static final QDashboardGenderStatistics dashboardGenderStatistics = new QDashboardGenderStatistics("dashboardGenderStatistics");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath date = createString("date");

    public final NumberPath<Long> femaleValue = createNumber("femaleValue", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> maleValue = createNumber("maleValue", Long.class);

    public final EnumPath<ag.act.enums.admin.DashboardStatisticsType> type = createEnum("type", ag.act.enums.admin.DashboardStatisticsType.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QDashboardGenderStatistics(String variable) {
        super(DashboardGenderStatistics.class, forVariable(variable));
    }

    public QDashboardGenderStatistics(Path<? extends DashboardGenderStatistics> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDashboardGenderStatistics(PathMetadata metadata) {
        super(DashboardGenderStatistics.class, metadata);
    }

}

