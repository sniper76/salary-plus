package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPopup is a Querydsl query type for Popup
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPopup extends EntityPathBase<Popup> {

    private static final long serialVersionUID = -644271259L;

    public static final QPopup popup = new QPopup("popup");

    public final EnumPath<ag.act.enums.BoardCategory> boardCategory = createEnum("boardCategory", ag.act.enums.BoardCategory.class);

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final EnumPath<ag.act.enums.popup.PopupDisplayTargetType> displayTargetType = createEnum("displayTargetType", ag.act.enums.popup.PopupDisplayTargetType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath linkTitle = createString("linkTitle");

    public final EnumPath<ag.act.enums.AppLinkType> linkType = createEnum("linkType", ag.act.enums.AppLinkType.class);

    public final StringPath linkUrl = createString("linkUrl");

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final StringPath stockCode = createString("stockCode");

    public final NumberPath<Long> stockGroupId = createNumber("stockGroupId", Long.class);

    public final EnumPath<ag.act.enums.push.PushTargetType> stockTargetType = createEnum("stockTargetType", ag.act.enums.push.PushTargetType.class);

    public final DateTimePath<java.time.LocalDateTime> targetEndDatetime = createDateTime("targetEndDatetime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> targetStartDatetime = createDateTime("targetStartDatetime", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QPopup(String variable) {
        super(Popup.class, forVariable(variable));
    }

    public QPopup(Path<? extends Popup> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPopup(PathMetadata metadata) {
        super(Popup.class, metadata);
    }

}

