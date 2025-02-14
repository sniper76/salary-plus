package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMyDataSummary is a Querydsl query type for MyDataSummary
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMyDataSummary extends EntityPathBase<MyDataSummary> {

    private static final long serialVersionUID = -940258039L;

    public static final QMyDataSummary myDataSummary = new QMyDataSummary("myDataSummary");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final SimplePath<ag.act.entity.mydata.JsonMyData> jsonMyData = createSimple("jsonMyData", ag.act.entity.mydata.JsonMyData.class);

    public final NumberPath<Long> loanPrice = createNumber("loanPrice", Long.class);

    public final NumberPath<Long> pensionPaidAmount = createNumber("pensionPaidAmount", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QMyDataSummary(String variable) {
        super(MyDataSummary.class, forVariable(variable));
    }

    public QMyDataSummary(Path<? extends MyDataSummary> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMyDataSummary(PathMetadata metadata) {
        super(MyDataSummary.class, metadata);
    }

}

