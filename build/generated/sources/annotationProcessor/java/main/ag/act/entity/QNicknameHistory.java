package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNicknameHistory is a Querydsl query type for NicknameHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNicknameHistory extends EntityPathBase<NicknameHistory> {

    private static final long serialVersionUID = 1898679231L;

    public static final QNicknameHistory nicknameHistory = new QNicknameHistory("nicknameHistory");

    public final BooleanPath byAdmin = createBoolean("byAdmin");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isFirst = createBoolean("isFirst");

    public final StringPath nickname = createString("nickname");

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QNicknameHistory(String variable) {
        super(NicknameHistory.class, forVariable(variable));
    }

    public QNicknameHistory(Path<? extends NicknameHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNicknameHistory(PathMetadata metadata) {
        super(NicknameHistory.class, metadata);
    }

}

