package ag.act.entity.digitaldocument;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDigitalDocumentDownload is a Querydsl query type for DigitalDocumentDownload
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDigitalDocumentDownload extends EntityPathBase<DigitalDocumentDownload> {

    private static final long serialVersionUID = 157512633L;

    public static final QDigitalDocumentDownload digitalDocumentDownload = new QDigitalDocumentDownload("digitalDocumentDownload");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.util.Date> deletedAt = createDateTime("deletedAt", java.util.Date.class);

    public final NumberPath<Long> digitalDocumentId = createNumber("digitalDocumentId", Long.class);

    public final NumberPath<Integer> downloadCount = createNumber("downloadCount", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isLatest = createBoolean("isLatest");

    public final NumberPath<Long> requestUserId = createNumber("requestUserId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final StringPath zipFileKey = createString("zipFileKey");

    public final StringPath zipFilePath = createString("zipFilePath");

    public final EnumPath<ag.act.enums.digitaldocument.ZipFileStatus> zipFileStatus = createEnum("zipFileStatus", ag.act.enums.digitaldocument.ZipFileStatus.class);

    public QDigitalDocumentDownload(String variable) {
        super(DigitalDocumentDownload.class, forVariable(variable));
    }

    public QDigitalDocumentDownload(Path<? extends DigitalDocumentDownload> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDigitalDocumentDownload(PathMetadata metadata) {
        super(DigitalDocumentDownload.class, metadata);
    }

}

