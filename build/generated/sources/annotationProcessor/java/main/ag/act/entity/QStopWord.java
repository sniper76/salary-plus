package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStopWord is a Querydsl query type for StopWord
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStopWord extends EntityPathBase<StopWord> {

    private static final long serialVersionUID = 1765902547L;

    public static final QStopWord stopWord = new QStopWord("stopWord");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final StringPath word = createString("word");

    public QStopWord(String variable) {
        super(StopWord.class, forVariable(variable));
    }

    public QStopWord(Path<? extends StopWord> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStopWord(PathMetadata metadata) {
        super(StopWord.class, metadata);
    }

}

