package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCampaign is a Querydsl query type for Campaign
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCampaign extends EntityPathBase<Campaign> {

    private static final long serialVersionUID = -88926281L;

    public static final QCampaign campaign = new QCampaign("campaign");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<ag.act.entity.campaign.CampaignDownload, ag.act.entity.campaign.QCampaignDownload> onlyOneLatestCampaignDownloads = this.<ag.act.entity.campaign.CampaignDownload, ag.act.entity.campaign.QCampaignDownload>createList("onlyOneLatestCampaignDownloads", ag.act.entity.campaign.CampaignDownload.class, ag.act.entity.campaign.QCampaignDownload.class, PathInits.DIRECT2);

    public final NumberPath<Long> sourcePostId = createNumber("sourcePostId", Long.class);

    public final NumberPath<Long> sourceStockGroupId = createNumber("sourceStockGroupId", Long.class);

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QCampaign(String variable) {
        super(Campaign.class, forVariable(variable));
    }

    public QCampaign(Path<? extends Campaign> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCampaign(PathMetadata metadata) {
        super(Campaign.class, metadata);
    }

}

