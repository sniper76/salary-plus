package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPollAnswer is a Querydsl query type for PollAnswer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPollAnswer extends EntityPathBase<PollAnswer> {

    private static final long serialVersionUID = 737919300L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPollAnswer pollAnswer = new QPollAnswer("pollAnswer");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> pollId = createNumber("pollId", Long.class);

    public final QPollItem pollItem;

    public final NumberPath<Long> pollItemId = createNumber("pollItemId", Long.class);

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final NumberPath<Long> stockQuantity = createNumber("stockQuantity", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QPollAnswer(String variable) {
        this(PollAnswer.class, forVariable(variable), INITS);
    }

    public QPollAnswer(Path<? extends PollAnswer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPollAnswer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPollAnswer(PathMetadata metadata, PathInits inits) {
        this(PollAnswer.class, metadata, inits);
    }

    public QPollAnswer(Class<? extends PollAnswer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.pollItem = inits.isInitialized("pollItem") ? new QPollItem(forProperty("pollItem"), inits.get("pollItem")) : null;
    }

}

