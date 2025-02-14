package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAutomatedAuthorPush is a Querydsl query type for AutomatedAuthorPush
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAutomatedAuthorPush extends EntityPathBase<AutomatedAuthorPush> {

    private static final long serialVersionUID = 903234574L;

    public static final QAutomatedAuthorPush automatedAuthorPush = new QAutomatedAuthorPush("automatedAuthorPush");

    public final NumberPath<Long> contentId = createNumber("contentId", Long.class);

    public final EnumPath<ag.act.enums.AutomatedPushContentType> contentType = createEnum("contentType", ag.act.enums.AutomatedPushContentType.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final EnumPath<ag.act.enums.AutomatedPushCriteria> criteria = createEnum("criteria", ag.act.enums.AutomatedPushCriteria.class);

    public final NumberPath<Integer> criteriaValue = createNumber("criteriaValue", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> pushId = createNumber("pushId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QAutomatedAuthorPush(String variable) {
        super(AutomatedAuthorPush.class, forVariable(variable));
    }

    public QAutomatedAuthorPush(Path<? extends AutomatedAuthorPush> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAutomatedAuthorPush(PathMetadata metadata) {
        super(AutomatedAuthorPush.class, metadata);
    }

}

