package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCorporateUser is a Querydsl query type for CorporateUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCorporateUser extends EntityPathBase<CorporateUser> {

    private static final long serialVersionUID = 508602249L;

    public static final QCorporateUser corporateUser = new QCorporateUser("corporateUser");

    public final StringPath corporateName = createString("corporateName");

    public final StringPath corporateNo = createString("corporateNo");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QCorporateUser(String variable) {
        super(CorporateUser.class, forVariable(variable));
    }

    public QCorporateUser(Path<? extends CorporateUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCorporateUser(PathMetadata metadata) {
        super(CorporateUser.class, metadata);
    }

}

