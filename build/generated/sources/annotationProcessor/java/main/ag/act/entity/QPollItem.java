package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPollItem is a Querydsl query type for PollItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPollItem extends EntityPathBase<PollItem> {

    private static final long serialVersionUID = 599893529L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPollItem pollItem = new QPollItem("pollItem");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPoll poll;

    public final ListPath<PollAnswer, QPollAnswer> pollAnswer = this.<PollAnswer, QPollAnswer>createList("pollAnswer", PollAnswer.class, QPollAnswer.class, PathInits.DIRECT2);

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final StringPath text = createString("text");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QPollItem(String variable) {
        this(PollItem.class, forVariable(variable), INITS);
    }

    public QPollItem(Path<? extends PollItem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPollItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPollItem(PathMetadata metadata, PathInits inits) {
        this(PollItem.class, metadata, inits);
    }

    public QPollItem(Class<? extends PollItem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.poll = inits.isInitialized("poll") ? new QPoll(forProperty("poll"), inits.get("poll")) : null;
    }

}

