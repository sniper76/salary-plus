package ag.act.entity.digitaldocument;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QHolderListReadAndCopy is a Querydsl query type for HolderListReadAndCopy
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHolderListReadAndCopy extends EntityPathBase<HolderListReadAndCopy> {

    private static final long serialVersionUID = -709886230L;

    public static final QHolderListReadAndCopy holderListReadAndCopy = new QHolderListReadAndCopy("holderListReadAndCopy");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> digitalDocumentId = createNumber("digitalDocumentId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<ag.act.enums.digitaldocument.HolderListReadAndCopyItemType> itemType = createEnum("itemType", ag.act.enums.digitaldocument.HolderListReadAndCopyItemType.class);

    public final StringPath itemValue = createString("itemValue");

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public QHolderListReadAndCopy(String variable) {
        super(HolderListReadAndCopy.class, forVariable(variable));
    }

    public QHolderListReadAndCopy(Path<? extends HolderListReadAndCopy> path) {
        super(path.getType(), path.getMetadata());
    }

    public QHolderListReadAndCopy(PathMetadata metadata) {
        super(HolderListReadAndCopy.class, metadata);
    }

}

