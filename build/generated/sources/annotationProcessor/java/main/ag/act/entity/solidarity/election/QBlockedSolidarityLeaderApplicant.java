package ag.act.entity.solidarity.election;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBlockedSolidarityLeaderApplicant is a Querydsl query type for BlockedSolidarityLeaderApplicant
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBlockedSolidarityLeaderApplicant extends EntityPathBase<BlockedSolidarityLeaderApplicant> {

    private static final long serialVersionUID = 178584955L;

    public static final QBlockedSolidarityLeaderApplicant blockedSolidarityLeaderApplicant = new QBlockedSolidarityLeaderApplicant("blockedSolidarityLeaderApplicant");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath reasons = createString("reasons");

    public final NumberPath<Long> solidarityId = createNumber("solidarityId", Long.class);

    public final StringPath stockCode = createString("stockCode");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QBlockedSolidarityLeaderApplicant(String variable) {
        super(BlockedSolidarityLeaderApplicant.class, forVariable(variable));
    }

    public QBlockedSolidarityLeaderApplicant(Path<? extends BlockedSolidarityLeaderApplicant> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBlockedSolidarityLeaderApplicant(PathMetadata metadata) {
        super(BlockedSolidarityLeaderApplicant.class, metadata);
    }

}

