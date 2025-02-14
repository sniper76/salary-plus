package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCampaignStockMapping is a Querydsl query type for CampaignStockMapping
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCampaignStockMapping extends EntityPathBase<CampaignStockMapping> {

    private static final long serialVersionUID = 71124527L;

    public static final QCampaignStockMapping campaignStockMapping = new QCampaignStockMapping("campaignStockMapping");

    public final NumberPath<Long> campaignId = createNumber("campaignId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath stockCode = createString("stockCode");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QCampaignStockMapping(String variable) {
        super(CampaignStockMapping.class, forVariable(variable));
    }

    public QCampaignStockMapping(Path<? extends CampaignStockMapping> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCampaignStockMapping(PathMetadata metadata) {
        super(CampaignStockMapping.class, metadata);
    }

}

