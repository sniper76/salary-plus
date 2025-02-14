package ag.act.entity.digitaldocument;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDigitalDocument is a Querydsl query type for DigitalDocument
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDigitalDocument extends EntityPathBase<DigitalDocument> {

    private static final long serialVersionUID = -1119164399L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDigitalDocument digitalDocument = new QDigitalDocument("digitalDocument");

    public final NumberPath<Long> acceptUserId = createNumber("acceptUserId", Long.class);

    public final StringPath companyName = createString("companyName");

    public final StringPath companyRegistrationNumber = createString("companyRegistrationNumber");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final StringPath designatedAgentNames = createString("designatedAgentNames");

    public final ListPath<DigitalDocumentItem, QDigitalDocumentItem> digitalDocumentItemList = this.<DigitalDocumentItem, QDigitalDocumentItem>createList("digitalDocumentItemList", DigitalDocumentItem.class, QDigitalDocumentItem.class, PathInits.DIRECT2);

    public final ListPath<DigitalDocumentUser, QDigitalDocumentUser> digitalDocumentUserList = this.<DigitalDocumentUser, QDigitalDocumentUser>createList("digitalDocumentUserList", DigitalDocumentUser.class, QDigitalDocumentUser.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<ag.act.enums.digitaldocument.IdCardWatermarkType> idCardWatermarkType = createEnum("idCardWatermarkType", ag.act.enums.digitaldocument.IdCardWatermarkType.class);

    public final BooleanPath isDisplayStockQuantity = createBoolean("isDisplayStockQuantity");

    public final NumberPath<Long> joinStockSum = createNumber("joinStockSum", Long.class);

    public final NumberPath<Integer> joinUserCount = createNumber("joinUserCount", Integer.class);

    public final SimplePath<ag.act.model.JsonAttachOption> jsonAttachOption = createSimple("jsonAttachOption", ag.act.model.JsonAttachOption.class);

    public final ListPath<DigitalDocumentDownload, QDigitalDocumentDownload> onlyOneLatestDigitalDocumentDownloads = this.<DigitalDocumentDownload, QDigitalDocumentDownload>createList("onlyOneLatestDigitalDocumentDownloads", DigitalDocumentDownload.class, QDigitalDocumentDownload.class, PathInits.DIRECT2);

    public final ag.act.entity.QPost post;

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> shareholderMeetingDate = createDateTime("shareholderMeetingDate", java.time.LocalDateTime.class);

    public final StringPath shareholderMeetingName = createString("shareholderMeetingName");

    public final StringPath shareholderMeetingType = createString("shareholderMeetingType");

    public final NumberPath<Double> shareholdingRatio = createNumber("shareholdingRatio", Double.class);

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final StringPath stockCode = createString("stockCode");

    public final DatePath<java.time.LocalDate> stockReferenceDate = createDate("stockReferenceDate", java.time.LocalDate.class);

    public final DateTimePath<java.time.LocalDateTime> targetEndDate = createDateTime("targetEndDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> targetStartDate = createDateTime("targetStartDate", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public final EnumPath<ag.act.enums.DigitalDocumentType> type = createEnum("type", ag.act.enums.DigitalDocumentType.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final EnumPath<ag.act.enums.DigitalDocumentVersion> version = createEnum("version", ag.act.enums.DigitalDocumentVersion.class);

    public QDigitalDocument(String variable) {
        this(DigitalDocument.class, forVariable(variable), INITS);
    }

    public QDigitalDocument(Path<? extends DigitalDocument> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDigitalDocument(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDigitalDocument(PathMetadata metadata, PathInits inits) {
        this(DigitalDocument.class, metadata, inits);
    }

    public QDigitalDocument(Class<? extends DigitalDocument> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new ag.act.entity.QPost(forProperty("post"), inits.get("post")) : null;
    }

}

