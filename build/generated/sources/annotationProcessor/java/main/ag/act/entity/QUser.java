package ag.act.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -1683198478L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final StringPath address = createString("address");

    public final StringPath addressDetail = createString("addressDetail");

    public final EnumPath<ag.act.model.AuthType> authType = createEnum("authType", ag.act.model.AuthType.class);

    public final DateTimePath<java.time.LocalDateTime> birthDate = createDateTime("birthDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> editedAt = createDateTime("editedAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Integer> firstNumberOfIdentification = createNumber("firstNumberOfIdentification", Integer.class);

    public final EnumPath<ag.act.model.Gender> gender = createEnum("gender", ag.act.model.Gender.class);

    public final StringPath hashedCI = createString("hashedCI");

    public final StringPath hashedDI = createString("hashedDI");

    public final StringPath hashedPhoneNumber = createString("hashedPhoneNumber");

    public final StringPath hashedPinNumber = createString("hashedPinNumber");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isAgreeToReceiveMail = createBoolean("isAgreeToReceiveMail");

    public final BooleanPath isChangePasswordRequired = createBoolean("isChangePasswordRequired");

    public final BooleanPath isSolidarityLeaderConfidentialAgreementSigned = createBoolean("isSolidarityLeaderConfidentialAgreementSigned");

    public final StringPath jobTitle = createString("jobTitle");

    public final DateTimePath<java.time.LocalDateTime> lastPinNumberVerifiedAt = createDateTime("lastPinNumberVerifiedAt", java.time.LocalDateTime.class);

    public final QMyDataSummary myDataSummary;

    public final StringPath mySpeech = createString("mySpeech");

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final ListPath<NicknameHistory, QNicknameHistory> nicknameHistories = this.<NicknameHistory, QNicknameHistory>createList("nicknameHistories", NicknameHistory.class, QNicknameHistory.class, PathInits.DIRECT2);

    public final QNicknameHistory nicknameHistory;

    public final StringPath password = createString("password");

    public final StringPath profileImageUrl = createString("profileImageUrl");

    public final StringPath pushToken = createString("pushToken");

    public final ListPath<UserRole, QUserRole> roles = this.<UserRole, QUserRole>createList("roles", UserRole.class, QUserRole.class, PathInits.DIRECT2);

    public final EnumPath<ag.act.model.Status> status = createEnum("status", ag.act.model.Status.class);

    public final NumberPath<Long> totalAssetAmount = createNumber("totalAssetAmount", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final ListPath<UserHoldingStock, QUserHoldingStock> userHoldingStocks = this.<UserHoldingStock, QUserHoldingStock>createList("userHoldingStocks", UserHoldingStock.class, QUserHoldingStock.class, PathInits.DIRECT2);

    public final StringPath zipcode = createString("zipcode");

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.myDataSummary = inits.isInitialized("myDataSummary") ? new QMyDataSummary(forProperty("myDataSummary")) : null;
        this.nicknameHistory = inits.isInitialized("nicknameHistory") ? new QNicknameHistory(forProperty("nicknameHistory")) : null;
    }

}

