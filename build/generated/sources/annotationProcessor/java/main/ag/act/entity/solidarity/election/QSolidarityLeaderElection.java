package ag.act.entity.solidarity.election;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSolidarityLeaderElection is a Querydsl query type for SolidarityLeaderElection
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSolidarityLeaderElection extends EntityPathBase<SolidarityLeaderElection> {

    private static final long serialVersionUID = -1429889164L;

    public static final QSolidarityLeaderElection solidarityLeaderElection = new QSolidarityLeaderElection("solidarityLeaderElection");

    public final NumberPath<Integer> candidateCount = createNumber("candidateCount", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> candidateRegistrationEndDateTime = createDateTime("candidateRegistrationEndDateTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> candidateRegistrationStartDateTime = createDateTime("candidateRegistrationStartDateTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> displayEndDateTime = createDateTime("displayEndDateTime", java.time.LocalDateTime.class);

    public final EnumPath<ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus> electionStatus = createEnum("electionStatus", ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus.class);

    public final EnumPath<ag.act.enums.solidarity.election.SolidarityLeaderElectionStatusDetails> electionStatusDetails = createEnum("electionStatusDetails", ag.act.enums.solidarity.election.SolidarityLeaderElectionStatusDetails.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final StringPath stockCode = createString("stockCode");

    public final NumberPath<Long> totalStockQuantity = createNumber("totalStockQuantity", Long.class);

    public final NumberPath<Double> totalVoteStake = createNumber("totalVoteStake", Double.class);

    public final NumberPath<Long> totalVoteStockQuantity = createNumber("totalVoteStockQuantity", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> voteClosingDateTime = createDateTime("voteClosingDateTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> voteEndDateTime = createDateTime("voteEndDateTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> voteStartDateTime = createDateTime("voteStartDateTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> winnerApplicantId = createNumber("winnerApplicantId", Long.class);

    public QSolidarityLeaderElection(String variable) {
        super(SolidarityLeaderElection.class, forVariable(variable));
    }

    public QSolidarityLeaderElection(Path<? extends SolidarityLeaderElection> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSolidarityLeaderElection(PathMetadata metadata) {
        super(SolidarityLeaderElection.class, metadata);
    }

}

