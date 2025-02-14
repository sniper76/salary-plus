package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = -1683350841L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPost post = new QPost("post");

    public final DateTimePath<java.time.LocalDateTime> activeEndDate = createDateTime("activeEndDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> activeStartDate = createDateTime("activeStartDate", java.time.LocalDateTime.class);

    public final StringPath anonymousName = createString("anonymousName");

    public final QBoard board;

    public final NumberPath<Long> boardId = createNumber("boardId", Long.class);

    public final EnumPath<ag.act.enums.ClientType> clientType = createEnum("clientType", ag.act.enums.ClientType.class);

    public final NumberPath<Long> commentCount = createNumber("commentCount", Long.class);

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final ag.act.entity.digitaldocument.QDigitalDocument digitalDocument;

    public final QDigitalProxy digitalProxy;

    public final DateTimePath<java.time.LocalDateTime> editedAt = createDateTime("editedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isAnonymous = createBoolean("isAnonymous");

    public final BooleanPath isExclusiveToHolders = createBoolean("isExclusiveToHolders");

    public final BooleanPath isNotification = createBoolean("isNotification");

    public final BooleanPath isPinned = createBoolean("isPinned");

    public final NumberPath<Long> likeCount = createNumber("likeCount", Long.class);

    public final ListPath<Poll, QPoll> polls = this.<Poll, QPoll>createList("polls", Poll.class, QPoll.class, PathInits.DIRECT2);

    public final QPostUserProfile postUserProfile;

    public final NumberPath<Long> pushId = createNumber("pushId", Long.class);

    public final NumberPath<Long> sourcePostId = createNumber("sourcePostId", Long.class);

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final StringPath thumbnailImageUrl = createString("thumbnailImageUrl");

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final NumberPath<Long> viewCount = createNumber("viewCount", Long.class);

    public final NumberPath<Long> viewUserCount = createNumber("viewUserCount", Long.class);

    public QPost(String variable) {
        this(Post.class, forVariable(variable), INITS);
    }

    public QPost(Path<? extends Post> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPost(PathMetadata metadata, PathInits inits) {
        this(Post.class, metadata, inits);
    }

    public QPost(Class<? extends Post> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.board = inits.isInitialized("board") ? new QBoard(forProperty("board"), inits.get("board")) : null;
        this.digitalDocument = inits.isInitialized("digitalDocument") ? new ag.act.entity.digitaldocument.QDigitalDocument(forProperty("digitalDocument"), inits.get("digitalDocument")) : null;
        this.digitalProxy = inits.isInitialized("digitalProxy") ? new QDigitalProxy(forProperty("digitalProxy"), inits.get("digitalProxy")) : null;
        this.postUserProfile = inits.isInitialized("postUserProfile") ? new QPostUserProfile(forProperty("postUserProfile")) : null;
    }

}

