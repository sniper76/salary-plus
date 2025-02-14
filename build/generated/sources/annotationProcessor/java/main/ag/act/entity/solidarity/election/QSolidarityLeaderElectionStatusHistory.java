package ag.act.entity.solidarity.election;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSolidarityLeaderElectionStatusHistory is a Querydsl query type for SolidarityLeaderElectionStatusHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSolidarityLeaderElectionStatusHistory extends EntityPathBase<SolidarityLeaderElectionStatusHistory> {

    private static final long serialVersionUID = -891058898L;

    public static final QSolidarityLeaderElectionStatusHistory solidarityLeaderElectionStatusHistory = new QSolidarityLeaderElectionStatusHistory("solidarityLeaderElectionStatusHistory");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final EnumPath<ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus> electionStatus = createEnum("electionStatus", ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus.class);

    public final EnumPath<ag.act.enums.solidarity.election.SolidarityLeaderElectionStatusDetails> electionStatusDetails = createEnum("electionStatusDetails", ag.act.enums.solidarity.election.SolidarityLeaderElectionStatusDetails.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isSlackNotificationSent = createBoolean("isSlackNotificationSent");

    public final NumberPath<Long> pushId = createNumber("pushId", Long.class);

    public final NumberPath<Long> solidarityLeaderElectionId = createNumber("solidarityLeaderElectionId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QSolidarityLeaderElectionStatusHistory(String variable) {
        super(SolidarityLeaderElectionStatusHistory.class, forVariable(variable));
    }

    public QSolidarityLeaderElectionStatusHistory(Path<? extends SolidarityLeaderElectionStatusHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSolidarityLeaderElectionStatusHistory(PathMetadata metadata) {
        super(SolidarityLeaderElectionStatusHistory.class, metadata);
    }

}

