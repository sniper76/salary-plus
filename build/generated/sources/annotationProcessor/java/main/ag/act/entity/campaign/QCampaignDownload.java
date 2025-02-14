package ag.act.entity.campaign;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCampaignDownload is a Querydsl query type for CampaignDownload
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCampaignDownload extends EntityPathBase<CampaignDownload> {

    private static final long serialVersionUID = -202484913L;

    public static final QCampaignDownload campaignDownload = new QCampaignDownload("campaignDownload");

    public final NumberPath<Long> campaignId = createNumber("campaignId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.util.Date> deletedAt = createDateTime("deletedAt", java.util.Date.class);

    public final NumberPath<Integer> downloadCount = createNumber("downloadCount", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isLatest = createBoolean("isLatest");

    public final NumberPath<Long> requestUserId = createNumber("requestUserId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final StringPath zipFileKey = createString("zipFileKey");

    public final StringPath zipFilePath = createString("zipFilePath");

    public final EnumPath<ag.act.enums.digitaldocument.ZipFileStatus> zipFileStatus = createEnum("zipFileStatus", ag.act.enums.digitaldocument.ZipFileStatus.class);

    public QCampaignDownload(String variable) {
        super(CampaignDownload.class, forVariable(variable));
    }

    public QCampaignDownload(Path<? extends CampaignDownload> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCampaignDownload(PathMetadata metadata) {
        super(CampaignDownload.class, metadata);
    }

}

