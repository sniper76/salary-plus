package ag.act.entity.digitaldocument;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDigitalDocumentItemUserAnswer is a Querydsl query type for DigitalDocumentItemUserAnswer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDigitalDocumentItemUserAnswer extends EntityPathBase<DigitalDocumentItemUserAnswer> {

    private static final long serialVersionUID = 610758989L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDigitalDocumentItemUserAnswer digitalDocumentItemUserAnswer = new QDigitalDocumentItemUserAnswer("digitalDocumentItemUserAnswer");

    public final EnumPath<ag.act.enums.DigitalAnswerType> answerSelectValue = createEnum("answerSelectValue", ag.act.enums.DigitalAnswerType.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final QDigitalDocumentItem digitalDocumentItem;

    public final NumberPath<Long> digitalDocumentItemId = createNumber("digitalDocumentItemId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QDigitalDocumentItemUserAnswer(String variable) {
        this(DigitalDocumentItemUserAnswer.class, forVariable(variable), INITS);
    }

    public QDigitalDocumentItemUserAnswer(Path<? extends DigitalDocumentItemUserAnswer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDigitalDocumentItemUserAnswer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDigitalDocumentItemUserAnswer(PathMetadata metadata, PathInits inits) {
        this(DigitalDocumentItemUserAnswer.class, metadata, inits);
    }

    public QDigitalDocumentItemUserAnswer(Class<? extends DigitalDocumentItemUserAnswer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.digitalDocumentItem = inits.isInitialized("digitalDocumentItem") ? new QDigitalDocumentItem(forProperty("digitalDocumentItem"), inits.get("digitalDocumentItem")) : null;
    }

}

