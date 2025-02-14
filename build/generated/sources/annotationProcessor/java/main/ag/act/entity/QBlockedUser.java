package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBlockedUser is a Querydsl query type for BlockedUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBlockedUser extends EntityPathBase<BlockedUser> {

    private static final long serialVersionUID = 92831696L;

    public static final QBlockedUser blockedUser = new QBlockedUser("blockedUser");

    public final NumberPath<Long> blockedUserId = createNumber("blockedUserId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QBlockedUser(String variable) {
        super(BlockedUser.class, forVariable(variable));
    }

    public QBlockedUser(Path<? extends BlockedUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBlockedUser(PathMetadata metadata) {
        super(BlockedUser.class, metadata);
    }

}

