package ag.act.sp.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPricePlan is a Querydsl query type for PricePlan
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPricePlan extends EntityPathBase<PricePlan> {

    private static final long serialVersionUID = 91151860L;

    public static final QPricePlan pricePlan = new QPricePlan("pricePlan");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> maxUserCount = createNumber("maxUserCount", Integer.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QPricePlan(String variable) {
        super(PricePlan.class, forVariable(variable));
    }

    public QPricePlan(Path<? extends PricePlan> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPricePlan(PathMetadata metadata) {
        super(PricePlan.class, metadata);
    }

}

