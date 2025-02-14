package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLatestUserPostsView is a Querydsl query type for LatestUserPostsView
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLatestUserPostsView extends EntityPathBase<LatestUserPostsView> {

    private static final long serialVersionUID = 2041229375L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLatestUserPostsView latestUserPostsView = new QLatestUserPostsView("latestUserPostsView");

    public final EnumPath<ag.act.enums.BoardCategory> boardCategory = createEnum("boardCategory", ag.act.enums.BoardCategory.class);

    public final EnumPath<ag.act.enums.BoardGroup> boardGroup = createEnum("boardGroup", ag.act.enums.BoardGroup.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<ag.act.enums.PostsViewType> postsViewType = createEnum("postsViewType", ag.act.enums.PostsViewType.class);

    public final QStock stock;

    public final DateTimePath<java.time.LocalDateTime> timestamp = createDateTime("timestamp", java.time.LocalDateTime.class);

    public final StringPath uniqueCombinedId = createString("uniqueCombinedId");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final QUser user;

    public QLatestUserPostsView(String variable) {
        this(LatestUserPostsView.class, forVariable(variable), INITS);
    }

    public QLatestUserPostsView(Path<? extends LatestUserPostsView> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLatestUserPostsView(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLatestUserPostsView(PathMetadata metadata, PathInits inits) {
        this(LatestUserPostsView.class, metadata, inits);
    }

    public QLatestUserPostsView(Class<? extends LatestUserPostsView> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.stock = inits.isInitialized("stock") ? new QStock(forProperty("stock"), inits.get("stock")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

