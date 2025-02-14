package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCommentUserLike is a Querydsl query type for CommentUserLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommentUserLike extends EntityPathBase<CommentUserLike> {

    private static final long serialVersionUID = -676911878L;

    public static final QCommentUserLike commentUserLike = new QCommentUserLike("commentUserLike");

    public final NumberPath<Long> commentId = createNumber("commentId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QCommentUserLike(String variable) {
        super(CommentUserLike.class, forVariable(variable));
    }

    public QCommentUserLike(Path<? extends CommentUserLike> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCommentUserLike(PathMetadata metadata) {
        super(CommentUserLike.class, metadata);
    }

}

