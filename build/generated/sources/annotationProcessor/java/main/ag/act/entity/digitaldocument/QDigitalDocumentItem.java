package ag.act.entity.digitaldocument;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDigitalDocumentItem is a Querydsl query type for DigitalDocumentItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDigitalDocumentItem extends EntityPathBase<DigitalDocumentItem> {

    private static final long serialVersionUID = -827758908L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDigitalDocumentItem digitalDocumentItem = new QDigitalDocumentItem("digitalDocumentItem");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final EnumPath<ag.act.enums.DigitalAnswerType> defaultSelectValue = createEnum("defaultSelectValue", ag.act.enums.DigitalAnswerType.class);

    public final QDigitalDocument digitalDocument;

    public final NumberPath<Long> digitalDocumentId = createNumber("digitalDocumentId", Long.class);

    public final ListPath<DigitalDocumentItemUserAnswer, QDigitalDocumentItemUserAnswer> digitalDocumentItemUserAnswerList = this.<DigitalDocumentItemUserAnswer, QDigitalDocumentItemUserAnswer>createList("digitalDocumentItemUserAnswerList", DigitalDocumentItemUserAnswer.class, QDigitalDocumentItemUserAnswer.class, PathInits.DIRECT2);

    public final NumberPath<Long> groupId = createNumber("groupId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isLastItem = createBoolean("isLastItem");

    public final NumberPath<Integer> itemLevel = createNumber("itemLevel", Integer.class);

    public final StringPath leaderDescription = createString("leaderDescription");

    public final NumberPath<Long> parentId = createNumber("parentId", Long.class);

    public final StringPath title = createString("title");

    public QDigitalDocumentItem(String variable) {
        this(DigitalDocumentItem.class, forVariable(variable), INITS);
    }

    public QDigitalDocumentItem(Path<? extends DigitalDocumentItem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDigitalDocumentItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDigitalDocumentItem(PathMetadata metadata, PathInits inits) {
        this(DigitalDocumentItem.class, metadata, inits);
    }

    public QDigitalDocumentItem(Class<? extends DigitalDocumentItem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.digitalDocument = inits.isInitialized("digitalDocument") ? new QDigitalDocument(forProperty("digitalDocument"), inits.get("digitalDocument")) : null;
    }

}

