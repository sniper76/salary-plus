package ag.act.entity.notification;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNotificationUserView is a Querydsl query type for NotificationUserView
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotificationUserView extends EntityPathBase<NotificationUserView> {

    private static final long serialVersionUID = 809700279L;

    public static final QNotificationUserView notificationUserView = new QNotificationUserView("notificationUserView");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> notificationId = createNumber("notificationId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QNotificationUserView(String variable) {
        super(NotificationUserView.class, forVariable(variable));
    }

    public QNotificationUserView(Path<? extends NotificationUserView> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotificationUserView(PathMetadata metadata) {
        super(NotificationUserView.class, metadata);
    }

}

