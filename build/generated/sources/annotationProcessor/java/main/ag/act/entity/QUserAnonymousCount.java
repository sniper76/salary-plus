package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserAnonymousCount is a Querydsl query type for UserAnonymousCount
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserAnonymousCount extends EntityPathBase<UserAnonymousCount> {

    private static final long serialVersionUID = -636251340L;

    public static final QUserAnonymousCount userAnonymousCount = new QUserAnonymousCount("userAnonymousCount");

    public final NumberPath<Integer> commentCount = createNumber("commentCount", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> postCount = createNumber("postCount", Integer.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final StringPath writeDate = createString("writeDate");

    public QUserAnonymousCount(String variable) {
        super(UserAnonymousCount.class, forVariable(variable));
    }

    public QUserAnonymousCount(Path<? extends UserAnonymousCount> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserAnonymousCount(PathMetadata metadata) {
        super(UserAnonymousCount.class, metadata);
    }

}

