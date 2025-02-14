package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTestStock is a Querydsl query type for TestStock
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTestStock extends EntityPathBase<TestStock> {

    private static final long serialVersionUID = 389581789L;

    public static final QTestStock testStock = new QTestStock("testStock");

    public final StringPath code = createString("code");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final StringPath name = createString("name");

    public final SimplePath<ag.act.model.JsonTestStockUser> solidarityLeader = createSimple("solidarityLeader", ag.act.model.JsonTestStockUser.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final ListPath<ag.act.model.JsonTestStockUser, SimplePath<ag.act.model.JsonTestStockUser>> users = this.<ag.act.model.JsonTestStockUser, SimplePath<ag.act.model.JsonTestStockUser>>createList("users", ag.act.model.JsonTestStockUser.class, SimplePath.class, PathInits.DIRECT2);

    public QTestStock(String variable) {
        super(TestStock.class, forVariable(variable));
    }

    public QTestStock(Path<? extends TestStock> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTestStock(PathMetadata metadata) {
        super(TestStock.class, metadata);
    }

}

