package ag.act.sp.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QShopUserMapping is a Querydsl query type for ShopUserMapping
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QShopUserMapping extends EntityPathBase<ShopUserMapping> {

    private static final long serialVersionUID = -409929201L;

    public static final QShopUserMapping shopUserMapping = new QShopUserMapping("shopUserMapping");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> shopId = createNumber("shopId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QShopUserMapping(String variable) {
        super(ShopUserMapping.class, forVariable(variable));
    }

    public QShopUserMapping(Path<? extends ShopUserMapping> path) {
        super(path.getType(), path.getMetadata());
    }

    public QShopUserMapping(PathMetadata metadata) {
        super(ShopUserMapping.class, metadata);
    }

}

