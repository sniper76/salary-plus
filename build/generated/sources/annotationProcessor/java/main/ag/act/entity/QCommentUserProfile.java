package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCommentUserProfile is a Querydsl query type for CommentUserProfile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommentUserProfile extends EntityPathBase<CommentUserProfile> {

    private static final long serialVersionUID = -1493764762L;

    public static final QCommentUserProfile commentUserProfile = new QCommentUserProfile("commentUserProfile");

    public final NumberPath<Long> commentId = createNumber("commentId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final StringPath individualStockCountLabel = createString("individualStockCountLabel");

    public final BooleanPath isSolidarityLeader = createBoolean("isSolidarityLeader");

    public final StringPath nickname = createString("nickname");

    public final StringPath profileImageUrl = createString("profileImageUrl");

    public final StringPath totalAssetLabel = createString("totalAssetLabel");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final StringPath userIp = createString("userIp");

    public QCommentUserProfile(String variable) {
        super(CommentUserProfile.class, forVariable(variable));
    }

    public QCommentUserProfile(Path<? extends CommentUserProfile> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCommentUserProfile(PathMetadata metadata) {
        super(CommentUserProfile.class, metadata);
    }

}

