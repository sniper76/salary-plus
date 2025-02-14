package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPoll is a Querydsl query type for Poll
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPoll extends EntityPathBase<Poll> {

    private static final long serialVersionUID = -1683351066L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPoll poll = new QPoll("poll");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<PollItem, QPollItem> pollItemList = this.<PollItem, QPollItem>createList("pollItemList", PollItem.class, QPollItem.class, PathInits.DIRECT2);

    public final QPost post;

    public final EnumPath<ag.act.enums.SelectionOption> selectionOption = createEnum("selectionOption", ag.act.enums.SelectionOption.class);

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final DateTimePath<java.time.LocalDateTime> targetEndDate = createDateTime("targetEndDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> targetStartDate = createDateTime("targetStartDate", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final EnumPath<ag.act.enums.VoteType> voteType = createEnum("voteType", ag.act.enums.VoteType.class);

    public QPoll(String variable) {
        this(Poll.class, forVariable(variable), INITS);
    }

    public QPoll(Path<? extends Poll> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPoll(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPoll(PathMetadata metadata, PathInits inits) {
        this(Poll.class, metadata, inits);
    }

    public QPoll(Class<? extends Poll> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
    }

}

