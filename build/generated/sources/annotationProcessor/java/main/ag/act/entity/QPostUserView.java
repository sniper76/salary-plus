package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPostUserView is a Querydsl query type for PostUserView
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostUserView extends EntityPathBase<PostUserView> {

    private static final long serialVersionUID = 2048623863L;

    public static final QPostUserView postUserView = new QPostUserView("postUserView");

    public final NumberPath<Long> count = createNumber("count", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QPostUserView(String variable) {
        super(PostUserView.class, forVariable(variable));
    }

    public QPostUserView(Path<? extends PostUserView> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPostUserView(PathMetadata metadata) {
        super(PostUserView.class, metadata);
    }

}

