package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPostUserProfile is a Querydsl query type for PostUserProfile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostUserProfile extends EntityPathBase<PostUserProfile> {

    private static final long serialVersionUID = -1695332841L;

    public static final QPostUserProfile postUserProfile = new QPostUserProfile("postUserProfile");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final StringPath individualStockCountLabel = createString("individualStockCountLabel");

    public final BooleanPath isSolidarityLeader = createBoolean("isSolidarityLeader");

    public final StringPath nickname = createString("nickname");

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final StringPath profileImageUrl = createString("profileImageUrl");

    public final StringPath totalAssetLabel = createString("totalAssetLabel");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final StringPath userIp = createString("userIp");

    public QPostUserProfile(String variable) {
        super(PostUserProfile.class, forVariable(variable));
    }

    public QPostUserProfile(Path<? extends PostUserProfile> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPostUserProfile(PathMetadata metadata) {
        super(PostUserProfile.class, metadata);
    }

}

