package ag.act.entity.digitaldocument;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDigitalDocumentNumber is a Querydsl query type for DigitalDocumentNumber
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDigitalDocumentNumber extends EntityPathBase<DigitalDocumentNumber> {

    private static final long serialVersionUID = -763060550L;

    public static final QDigitalDocumentNumber digitalDocumentNumber = new QDigitalDocumentNumber("digitalDocumentNumber");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> digitalDocumentId = createNumber("digitalDocumentId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> lastIssuedNumber = createNumber("lastIssuedNumber", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QDigitalDocumentNumber(String variable) {
        super(DigitalDocumentNumber.class, forVariable(variable));
    }

    public QDigitalDocumentNumber(Path<? extends DigitalDocumentNumber> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDigitalDocumentNumber(PathMetadata metadata) {
        super(DigitalDocumentNumber.class, metadata);
    }

}

