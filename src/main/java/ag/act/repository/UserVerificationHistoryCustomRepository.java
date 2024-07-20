package ag.act.repository;

import java.time.LocalDateTime;

public interface UserVerificationHistoryCustomRepository {
    long countMyDataConnectedUserPinRegistrationBetween(LocalDateTime startDate, LocalDateTime endDate);

    long countPinVerificationOfMyDataConnectedUserRegisteredDuringReferenceWeek(
        LocalDateTime referenceWeekStartDate,
        LocalDateTime referenceWeekNextWeek,
        LocalDateTime startDate,
        LocalDateTime endDate
    );

    long countDigitalDocumentSaveOfReferenceWeekRegisteredUsersUntilEndDate(
        LocalDateTime referenceWeekStartDate,
        LocalDateTime referenceWeekNextWeek,
        LocalDateTime startDate,
        LocalDateTime endDate
    );


}
