package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStockGroup is a Querydsl query type for StockGroup
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStockGroup extends EntityPathBase<StockGroup> {

    private static final long serialVersionUID = -1891691024L;

    public static final QStockGroup stockGroup = new QStockGroup("stockGroup");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QStockGroup(String variable) {
        super(StockGroup.class, forVariable(variable));
    }

    public QStockGroup(Path<? extends StockGroup> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStockGroup(PathMetadata metadata) {
        super(StockGroup.class, metadata);
    }

}

