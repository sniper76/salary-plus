package ag.act.sp.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QShopGroup is a Querydsl query type for ShopGroup
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QShopGroup extends EntityPathBase<ShopGroup> {

    private static final long serialVersionUID = 91066123L;

    public static final QShopGroup shopGroup = new QShopGroup("shopGroup");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QShopGroup(String variable) {
        super(ShopGroup.class, forVariable(variable));
    }

    public QShopGroup(Path<? extends ShopGroup> path) {
        super(path.getType(), path.getMetadata());
    }

    public QShopGroup(PathMetadata metadata) {
        super(ShopGroup.class, metadata);
    }

}

