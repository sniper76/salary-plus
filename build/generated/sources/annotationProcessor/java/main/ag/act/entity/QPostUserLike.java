package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPostUserLike is a Querydsl query type for PostUserLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostUserLike extends EntityPathBase<PostUserLike> {

    private static final long serialVersionUID = 2048326121L;

    public static final QPostUserLike postUserLike = new QPostUserLike("postUserLike");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QPostUserLike(String variable) {
        super(PostUserLike.class, forVariable(variable));
    }

    public QPostUserLike(Path<? extends PostUserLike> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPostUserLike(PathMetadata metadata) {
        super(PostUserLike.class, metadata);
    }

}

