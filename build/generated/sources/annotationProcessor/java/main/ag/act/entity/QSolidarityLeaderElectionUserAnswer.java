package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSolidarityLeaderElectionUserAnswer is a Querydsl query type for SolidarityLeaderElectionUserAnswer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSolidarityLeaderElectionUserAnswer extends EntityPathBase<SolidarityLeaderElectionUserAnswer> {

    private static final long serialVersionUID = 916527716L;

    public static final QSolidarityLeaderElectionUserAnswer solidarityLeaderElectionUserAnswer = new QSolidarityLeaderElectionUserAnswer("solidarityLeaderElectionUserAnswer");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> solidarityLeaderApplicantId = createNumber("solidarityLeaderApplicantId", Long.class);

    public final NumberPath<Long> solidarityLeaderElectionId = createNumber("solidarityLeaderElectionId", Long.class);

    public final NumberPath<Long> stockQuantity = createNumber("stockQuantity", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QSolidarityLeaderElectionUserAnswer(String variable) {
        super(SolidarityLeaderElectionUserAnswer.class, forVariable(variable));
    }

    public QSolidarityLeaderElectionUserAnswer(Path<? extends SolidarityLeaderElectionUserAnswer> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSolidarityLeaderElectionUserAnswer(PathMetadata metadata) {
        super(SolidarityLeaderElectionUserAnswer.class, metadata);
    }

}

