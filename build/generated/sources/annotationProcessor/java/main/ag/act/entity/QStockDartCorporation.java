package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStockDartCorporation is a Querydsl query type for StockDartCorporation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStockDartCorporation extends EntityPathBase<StockDartCorporation> {

    private static final long serialVersionUID = -861517510L;

    public static final QStockDartCorporation stockDartCorporation = new QStockDartCorporation("stockDartCorporation");

    public final StringPath accountSettlementMonth = createString("accountSettlementMonth");

    public final StringPath address = createString("address");

    public final StringPath businessRegistrationNumber = createString("businessRegistrationNumber");

    public final StringPath ceoName = createString("ceoName");

    public final StringPath corpClass = createString("corpClass");

    public final StringPath corpCode = createString("corpCode");

    public final StringPath corpName = createString("corpName");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final StringPath establishmentDate = createString("establishmentDate");

    public final StringPath homepageUrl = createString("homepageUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath industryCode = createString("industryCode");

    public final StringPath irUrl = createString("irUrl");

    public final StringPath jurisdictionalRegistrationNumber = createString("jurisdictionalRegistrationNumber");

    public final StringPath modifyDate = createString("modifyDate");

    public final StringPath representativeFaxNumber = createString("representativeFaxNumber");

    public final StringPath representativePhoneNumber = createString("representativePhoneNumber");

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final StringPath stockCode = createString("stockCode");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public QStockDartCorporation(String variable) {
        super(StockDartCorporation.class, forVariable(variable));
    }

    public QStockDartCorporation(Path<? extends StockDartCorporation> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStockDartCorporation(PathMetadata metadata) {
        super(StockDartCorporation.class, metadata);
    }

}

