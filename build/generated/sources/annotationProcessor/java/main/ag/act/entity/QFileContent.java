package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFileContent is a Querydsl query type for FileContent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFileContent extends EntityPathBase<FileContent> {

    private static final long serialVersionUID = 1558579030L;

    public static final QFileContent fileContent = new QFileContent("fileContent");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final EnumPath<ag.act.enums.FileContentType> fileContentType = createEnum("fileContentType", ag.act.enums.FileContentType.class);

    public final StringPath filename = createString("filename");

    public final EnumPath<ag.act.enums.FileType> fileType = createEnum("fileType", ag.act.enums.FileType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath mimetype = createString("mimetype");

    public final StringPath originalFilename = createString("originalFilename");

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QFileContent(String variable) {
        super(FileContent.class, forVariable(variable));
    }

    public QFileContent(Path<? extends FileContent> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFileContent(PathMetadata metadata) {
        super(FileContent.class, metadata);
    }

}

