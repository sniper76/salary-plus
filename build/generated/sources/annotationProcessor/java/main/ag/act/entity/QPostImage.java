package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPostImage is a Querydsl query type for PostImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostImage extends EntityPathBase<PostImage> {

    private static final long serialVersionUID = -731757868L;

    public static final QPostImage postImage = new QPostImage("postImage");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> imageId = createNumber("imageId", Long.class);

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QPostImage(String variable) {
        super(PostImage.class, forVariable(variable));
    }

    public QPostImage(Path<? extends PostImage> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPostImage(PathMetadata metadata) {
        super(PostImage.class, metadata);
    }

}

