package ag.act.entity.digitaldocument;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDigitalDocumentUser is a Querydsl query type for DigitalDocumentUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDigitalDocumentUser extends EntityPathBase<DigitalDocumentUser> {

    private static final long serialVersionUID = -827402372L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDigitalDocumentUser digitalDocumentUser = new QDigitalDocumentUser("digitalDocumentUser");

    public final StringPath address = createString("address");

    public final StringPath addressDetail = createString("addressDetail");

    public final NumberPath<Long> attachmentPageCount = createNumber("attachmentPageCount", Long.class);

    public final DateTimePath<java.time.LocalDateTime> birthDate = createDateTime("birthDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final QDigitalDocument digitalDocument;

    public final EnumPath<ag.act.enums.DigitalDocumentAnswerStatus> digitalDocumentAnswerStatus = createEnum("digitalDocumentAnswerStatus", ag.act.enums.DigitalDocumentAnswerStatus.class);

    public final NumberPath<Long> digitalDocumentId = createNumber("digitalDocumentId", Long.class);

    public final NumberPath<Integer> firstNumberOfIdentification = createNumber("firstNumberOfIdentification", Integer.class);

    public final EnumPath<ag.act.model.Gender> gender = createEnum("gender", ag.act.model.Gender.class);

    public final StringPath hashedPhoneNumber = createString("hashedPhoneNumber");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> issuedNumber = createNumber("issuedNumber", Long.class);

    public final NumberPath<Long> loanPrice = createNumber("loanPrice", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<Long> originalPageCount = createNumber("originalPageCount", Long.class);

    public final StringPath pdfPath = createString("pdfPath");

    public final NumberPath<Long> purchasePrice = createNumber("purchasePrice", Long.class);

    public final StringPath stockCode = createString("stockCode");

    public final NumberPath<Long> stockCount = createNumber("stockCount", Long.class);

    public final StringPath stockName = createString("stockName");

    public final DatePath<java.time.LocalDate> stockReferenceDate = createDate("stockReferenceDate", java.time.LocalDate.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final StringPath zipcode = createString("zipcode");

    public QDigitalDocumentUser(String variable) {
        this(DigitalDocumentUser.class, forVariable(variable), INITS);
    }

    public QDigitalDocumentUser(Path<? extends DigitalDocumentUser> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDigitalDocumentUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDigitalDocumentUser(PathMetadata metadata, PathInits inits) {
        this(DigitalDocumentUser.class, metadata, inits);
    }

    public QDigitalDocumentUser(Class<? extends DigitalDocumentUser> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.digitalDocument = inits.isInitialized("digitalDocument") ? new QDigitalDocument(forProperty("digitalDocument"), inits.get("digitalDocument")) : null;
    }

}

