package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSolidarityLeaderApplicant is a Querydsl query type for SolidarityLeaderApplicant
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSolidarityLeaderApplicant extends EntityPathBase<SolidarityLeaderApplicant> {

    private static final long serialVersionUID = -735898944L;

    public static final QSolidarityLeaderApplicant solidarityLeaderApplicant = new QSolidarityLeaderApplicant("solidarityLeaderApplicant");

    public final EnumPath<ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus> applyStatus = createEnum("applyStatus", ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus.class);

    public final StringPath commentsForStockHolder = createString("commentsForStockHolder");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath goals = createString("goals");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath knowledgeOfCompanyManagement = createString("knowledgeOfCompanyManagement");

    public final StringPath reasonsForApplying = createString("reasonsForApplying");

    public final NumberPath<Long> solidarityId = createNumber("solidarityId", Long.class);

    public final NumberPath<Long> solidarityLeaderElectionId = createNumber("solidarityLeaderElectionId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final NumberPath<Integer> version = createNumber("version", Integer.class);

    public QSolidarityLeaderApplicant(String variable) {
        super(SolidarityLeaderApplicant.class, forVariable(variable));
    }

    public QSolidarityLeaderApplicant(Path<? extends SolidarityLeaderApplicant> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSolidarityLeaderApplicant(PathMetadata metadata) {
        super(SolidarityLeaderApplicant.class, metadata);
    }

}

