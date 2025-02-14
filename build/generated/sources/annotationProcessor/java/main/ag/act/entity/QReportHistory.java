package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QReportHistory is a Querydsl query type for ReportHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReportHistory extends EntityPathBase<ReportHistory> {

    private static final long serialVersionUID = -48456071L;

    public static final QReportHistory reportHistory = new QReportHistory("reportHistory");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> reportId = createNumber("reportId", Long.class);

    public final EnumPath<ag.act.model.ReportStatus> reportStatus = createEnum("reportStatus", ag.act.model.ReportStatus.class);

    public final StringPath result = createString("result");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QReportHistory(String variable) {
        super(ReportHistory.class, forVariable(variable));
    }

    public QReportHistory(Path<? extends ReportHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReportHistory(PathMetadata metadata) {
        super(ReportHistory.class, metadata);
    }

}

