package ag.act.entity.solidarity.election;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSolidarityLeaderElectionPollItemMapping is a Querydsl query type for SolidarityLeaderElectionPollItemMapping
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSolidarityLeaderElectionPollItemMapping extends EntityPathBase<SolidarityLeaderElectionPollItemMapping> {

    private static final long serialVersionUID = -117965080L;

    public static final QSolidarityLeaderElectionPollItemMapping solidarityLeaderElectionPollItemMapping = new QSolidarityLeaderElectionPollItemMapping("solidarityLeaderElectionPollItemMapping");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final EnumPath<ag.act.enums.solidarity.election.SolidarityLeaderElectionAnswerType> electionAnswerType = createEnum("electionAnswerType", ag.act.enums.solidarity.election.SolidarityLeaderElectionAnswerType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> pollItemId = createNumber("pollItemId", Long.class);

    public final NumberPath<Long> solidarityLeaderApplicantId = createNumber("solidarityLeaderApplicantId", Long.class);

    public final NumberPath<Long> solidarityLeaderElectionId = createNumber("solidarityLeaderElectionId", Long.class);

    public QSolidarityLeaderElectionPollItemMapping(String variable) {
        super(SolidarityLeaderElectionPollItemMapping.class, forVariable(variable));
    }

    public QSolidarityLeaderElectionPollItemMapping(Path<? extends SolidarityLeaderElectionPollItemMapping> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSolidarityLeaderElectionPollItemMapping(PathMetadata metadata) {
        super(SolidarityLeaderElectionPollItemMapping.class, metadata);
    }

}

