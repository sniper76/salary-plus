package ag.act.repository;

import ag.act.entity.QMyDataSummary;
import ag.act.entity.QUserVerificationHistory;
import ag.act.entity.UserVerificationHistory;
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@SuppressWarnings("localVariableName")
@Repository
public class UserVerificationHistoryCustomRepositoryImpl extends QuerydslRepositorySupport
    implements UserVerificationHistoryCustomRepository {

    public UserVerificationHistoryCustomRepositoryImpl() {
        super(UserVerificationHistory.class);
    }

    @Override
    public long countMyDataConnectedUserPinRegistrationBetween(
        LocalDateTime startDate,
        LocalDateTime endDate
    ) {
        QUserVerificationHistory uvh = QUserVerificationHistory.userVerificationHistory;
        JPQLQuery<Long> countMyDataConnectedRegistrationQuery = getCountMyDataConnectedRegistrationQuery(uvh, startDate, endDate);

        return Optional.ofNullable(
            countMyDataConnectedRegistrationQuery
                .fetchFirst()
        ).orElse(0L);
    }

    @Override
    public long countPinVerificationOfMyDataConnectedUserRegisteredDuringReferenceWeek(
        LocalDateTime referenceWeekStartDate,
        LocalDateTime referenceWeekNextWeek,
        LocalDateTime startDate,
        LocalDateTime endDate
    ) {
        QUserVerificationHistory uvh1 = new QUserVerificationHistory("uvh1");
        QUserVerificationHistory uvh2 = new QUserVerificationHistory("uvh2");
        JPQLQuery<Long> countPinVerificationQuery =
            getCountPinVerificationQuery(
                uvh1,
                uvh2,
                referenceWeekStartDate,
                referenceWeekNextWeek,
                startDate,
                endDate
            );

        return Optional.ofNullable(
            countPinVerificationQuery.fetchFirst()
        ).orElse(0L);
    }

    @Override
    public long countDigitalDocumentSaveOfReferenceWeekRegisteredUsersUntilEndDate(
        LocalDateTime referenceWeekStartDate,
        LocalDateTime referenceWeekNextWeek,
        LocalDateTime startDate,
        LocalDateTime endDate
    ) {
        QUserVerificationHistory uvh1 = new QUserVerificationHistory("uvh1");
        QUserVerificationHistory uvh2 = new QUserVerificationHistory("uvh2");
        QUserVerificationHistory uvh3 = new QUserVerificationHistory("uvh3");

        JPQLQuery<Long> countPinVerificationQuery =
            getCountPinVerificationQuery(
                uvh1,
                uvh2,
                referenceWeekStartDate,
                referenceWeekNextWeek,
                startDate,
                endDate
            );

        JPQLQuery<Long> countDigitalDocumentSaveQuery =
            countPinVerificationQuery.innerJoin(uvh3)
                .on(uvh1.userId.eq(uvh3.userId))
                .where(uvh3.verificationType.eq(VerificationType.SIGNATURE)
                    .and(uvh3.operationType.eq(VerificationOperationType.SIGNATURE))
                    .and(uvh3.createdAt.goe(referenceWeekStartDate))
                    .and(uvh3.createdAt.lt(endDate)));

        return Optional.ofNullable(
            countDigitalDocumentSaveQuery.fetchFirst()
        ).orElse(0L);
    }

    private JPQLQuery<Long> getCountPinVerificationQuery(
        QUserVerificationHistory uvh1,
        QUserVerificationHistory uvh2,
        LocalDateTime referenceWeekStartDate,
        LocalDateTime referenceWeekNextWeek,
        LocalDateTime startDate,
        LocalDateTime endDate
    ) {
        JPQLQuery<Long> countReferenceWeekRegisteredUserQuery =
            getCountMyDataConnectedRegistrationQuery(
                uvh1,
                referenceWeekStartDate,
                referenceWeekNextWeek
            );

        return countReferenceWeekRegisteredUserQuery
            .innerJoin(uvh2)
            .on(uvh1.userId.eq(uvh2.userId))
            .where(
                uvh2.operationType.eq(VerificationOperationType.VERIFICATION)
                    .and(uvh2.verificationType.eq(VerificationType.PIN))
                    .and(uvh2.createdAt.goe(startDate))
                    .and(uvh2.createdAt.lt(endDate))
            );
    }

    private JPQLQuery<Long> getCountMyDataConnectedRegistrationQuery(
        QUserVerificationHistory uvh,
        LocalDateTime startDate,
        LocalDateTime endDate
    ) {
        QMyDataSummary mds = QMyDataSummary.myDataSummary;

        return from(uvh)
            .innerJoin(mds).on(mds.userId.eq(uvh.userId))
            .where(uvh.verificationType.eq(VerificationType.PIN)
                .and(uvh.operationType.eq(VerificationOperationType.REGISTER))
                .and(uvh.createdAt.goe(startDate))
                .and(uvh.createdAt.lt(endDate)))
            .select(uvh.userId.countDistinct());
    }
}
