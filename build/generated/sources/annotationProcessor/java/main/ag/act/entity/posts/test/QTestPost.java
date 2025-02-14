package ag.act.entity.posts.test;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTestPost is a Querydsl query type for TestPost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTestPost extends EntityPathBase<TestPost> {

    private static final long serialVersionUID = 1090263692L;

    public static final QTestPost testPost = new QTestPost("testPost");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> targetEndDate = createDateTime("targetEndDate", java.time.LocalDateTime.class);

    public QTestPost(String variable) {
        super(TestPost.class, forVariable(variable));
    }

    public QTestPost(Path<? extends TestPost> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTestPost(PathMetadata metadata) {
        super(TestPost.class, metadata);
    }

}

