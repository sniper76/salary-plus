package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBatchLog is a Querydsl query type for BatchLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBatchLog extends EntityPathBase<BatchLog> {

    private static final long serialVersionUID = -1643157423L;

    public static final QBatchLog batchLog = new QBatchLog("batchLog");

    public final StringPath batchGroupName = createString("batchGroupName");

    public final StringPath batchName = createString("batchName");

    public final NumberPath<Integer> batchPeriod = createNumber("batchPeriod", Integer.class);

    public final EnumPath<ag.act.enums.BatchStatus> batchStatus = createEnum("batchStatus", ag.act.enums.BatchStatus.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> endTime = createDateTime("endTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<ag.act.model.BatchRequest.PeriodTimeUnitEnum> periodTimeUnit = createEnum("periodTimeUnit", ag.act.model.BatchRequest.PeriodTimeUnitEnum.class);

    public final StringPath result = createString("result");

    public final DateTimePath<java.time.LocalDateTime> startTime = createDateTime("startTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QBatchLog(String variable) {
        super(BatchLog.class, forVariable(variable));
    }

    public QBatchLog(Path<? extends BatchLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBatchLog(PathMetadata metadata) {
        super(BatchLog.class, metadata);
    }

}

