package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAppPreference is a Querydsl query type for AppPreference
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAppPreference extends EntityPathBase<AppPreference> {

    private static final long serialVersionUID = -66611307L;

    public static final QAppPreference appPreference = new QAppPreference("appPreference");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> createdBy = createNumber("createdBy", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<ag.act.enums.AppPreferenceType> type = createEnum("type", ag.act.enums.AppPreferenceType.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> updatedBy = createNumber("updatedBy", Long.class);

    public final StringPath value = createString("value");

    public QAppPreference(String variable) {
        super(AppPreference.class, forVariable(variable));
    }

    public QAppPreference(Path<? extends AppPreference> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAppPreference(PathMetadata metadata) {
        super(AppPreference.class, metadata);
    }

}

