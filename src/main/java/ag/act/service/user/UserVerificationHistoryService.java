package ag.act.service.user;

import ag.act.core.holder.RequestContextHolder;
import ag.act.entity.UserVerificationHistory;
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import ag.act.repository.UserVerificationHistoryCustomRepository;
import ag.act.repository.UserVerificationHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserVerificationHistoryService {
    private final UserVerificationHistoryRepository userVerificationHistoryRepository;
    private final UserVerificationHistoryCustomRepository userVerificationHistoryCustomRepository;

    public UserVerificationHistory create(Long userId, VerificationType verificationType, VerificationOperationType operationType) {
        return create(userId, verificationType, operationType, null);
    }

    public UserVerificationHistory create(
        Long userId, VerificationType verificationType, VerificationOperationType operationType,
        Long digitalDocumentUserId
    ) {
        UserVerificationHistory userVerificationHistory = new UserVerificationHistory();
        userVerificationHistory.setUserId(userId);
        userVerificationHistory.setVerificationType(verificationType);
        userVerificationHistory.setOperationType(operationType);
        userVerificationHistory.setUserIp(RequestContextHolder.getUserIP());
        userVerificationHistory.setDigitalDocumentUserId(digitalDocumentUserId);

        return userVerificationHistoryRepository.save(userVerificationHistory);
    }

    public Optional<UserVerificationHistory> findLatestHistory(
        Long userId, VerificationType verificationType, VerificationOperationType operationType
    ) {
        return userVerificationHistoryRepository
            .findFirstByUserIdAndVerificationTypeAndOperationTypeOrderByCreatedAtDesc(
                userId, verificationType, operationType
            );
    }

    public Optional<UserVerificationHistory> findLatestDigitalDocumentSpecificHistory(
        Long userId, VerificationType verificationType, VerificationOperationType operationType, Long digitalDocumentUserId
    ) {
        return userVerificationHistoryRepository
            .findFirstByUserIdAndVerificationTypeAndOperationTypeAndDigitalDocumentUserIdOrderByCreatedAtDesc(
                userId, verificationType, operationType, digitalDocumentUserId
            );
    }

    public Optional<UserVerificationHistory> findFirstVerificationHistory(
        Long userId, VerificationType verificationType, VerificationOperationType operationType
    ) {
        return userVerificationHistoryRepository
            .findFirstByUserIdAndVerificationTypeAndOperationTypeOrderByCreatedAtAsc(
                userId, verificationType, operationType
            );
    }

    public double countMyDataConnectedUserPinRegistrationBetween(
        LocalDateTime startDateTime, LocalDateTime endDateTime
    ) {
        return userVerificationHistoryCustomRepository
            .countMyDataConnectedUserPinRegistrationBetween(
                startDateTime,
                endDateTime
            );
    }

    public double countMyDataConnectedUserPinVerificationWeekly(
        LocalDateTime referenceWeekDateTime,
        LocalDateTime startDateTime
    ) {
        return userVerificationHistoryCustomRepository
            .countPinVerificationOfMyDataConnectedUserRegisteredDuringReferenceWeek(
                referenceWeekDateTime,
                referenceWeekDateTime.plusWeeks(1),
                startDateTime,
                startDateTime.plusWeeks(1)
            );
    }

    public double countDigitalDocumentParticipation(
        LocalDateTime referenceWeekDateTime,
        LocalDateTime startDateTime
    ) {
        return userVerificationHistoryCustomRepository
            .countDigitalDocumentSaveOfReferenceWeekRegisteredUsersUntilEndDate(
                referenceWeekDateTime,
                referenceWeekDateTime.plusWeeks(1),
                startDateTime,
                startDateTime.plusWeeks(1)
            );
    }

    public double countFirstDigitalDocumentSaveDuring(
        LocalDateTime startDateTime
    ) {
        return userVerificationHistoryRepository
            .countFirstDigitalDocumentSaveDuring(
                startDateTime,
                startDateTime.plusWeeks(1)
            );
    }

    public double countPinVerificationWeeklyGivenFirstDigitalDocumentSave(
        LocalDateTime referenceWeekDateTime,
        LocalDateTime startDateTime
    ) {
        return userVerificationHistoryRepository
            .countPinVerificationWeeklyGivenFirstDigitalDocumentSaveDuring(
                referenceWeekDateTime,
                referenceWeekDateTime.plusWeeks(1),
                startDateTime,
                startDateTime.plusWeeks(1)
            );
    }

    public double countDigitalDocumentCompletedUserPinRegistrationBetween(
        LocalDateTime startDateTime, LocalDateTime endDateTime
    ) {
        return userVerificationHistoryRepository
            .countUserWithDigitalDocumentSignaturePinRegistrationBetween(
                startDateTime,
                endDateTime
            );
    }

    public double countDigitalDocumentCompletedUserPinVerificationWeekly(
        LocalDateTime referenceWeekDateTime,
        LocalDateTime startDateTime
    ) {
        return userVerificationHistoryRepository
            .countPinVerificationOfUserWithDigitalDocumentSignatureRegisteredDuringReferenceWeek(
                referenceWeekDateTime,
                referenceWeekDateTime.plusWeeks(1),
                startDateTime,
                startDateTime.plusWeeks(1)
            );
    }

    public double countNoDigitalDocumentCompletedUserPinRegistrationBetween(
        LocalDateTime startDateTime, LocalDateTime endDateTime
    ) {
        return userVerificationHistoryRepository
            .countUserWithoutDigitalDocumentSignaturePinRegistrationBetween(
                startDateTime,
                endDateTime
            );
    }

    public double countNoDigitalDocumentCompletedUserPinVerificationWeekly(
        LocalDateTime referenceWeekDateTime,
        LocalDateTime startDateTime
    ) {
        return userVerificationHistoryRepository
            .countPinVerificationOfUserWithoutDigitalDocumentSignatureRegisteredDuringReferenceWeek(
                referenceWeekDateTime,
                referenceWeekDateTime.plusWeeks(1),
                startDateTime,
                startDateTime.plusWeeks(1)
            );
    }

    public double countDigitalDocumentCompletedAndNonRetainedUserWeekly(
        LocalDateTime referenceWeekDateTime,
        LocalDateTime startDateTime
    ) {
        return userVerificationHistoryRepository
            .countNonRetainedUserWithDigitalDocumentSignatureRegisteredDuringReferenceWeek(
                referenceWeekDateTime,
                referenceWeekDateTime.plusWeeks(1),
                startDateTime,
                startDateTime.plusWeeks(1)
            );
    }

    public double countNoDigitalDocumentCompletedAndNonRetainedUserWeekly(
        LocalDateTime referenceWeekDateTime,
        LocalDateTime startDateTime
    ) {
        return userVerificationHistoryRepository
            .countNonRetainedUserWithoutDigitalDocumentSignatureRegisteredDuringReferenceWeek(
                referenceWeekDateTime,
                referenceWeekDateTime.plusWeeks(1),
                startDateTime,
                startDateTime.plusWeeks(1)
            );
    }

    public double countDigitalDocumentCompletedAndPinVerificationUserForThreeWeeksInARow(
        LocalDateTime referenceWeekDateTime,
        LocalDateTime startDateTime
    ) {
        return userVerificationHistoryRepository.countPinVerificationOfUserForThreeWeeksInARowWithDigitalDocumentSignature(
            referenceWeekDateTime,
            referenceWeekDateTime.plusWeeks(1),
            startDateTime,
            startDateTime.plusWeeks(1),
            startDateTime.plusWeeks(2),
            startDateTime.plusWeeks(3)
        );
    }

    public double countNoDigitalDocumentCompletedUserPinVerificationForThreeWeeksInARow(
        LocalDateTime referenceWeekDateTime,
        LocalDateTime startDateTime
    ) {
        return userVerificationHistoryRepository.countPinVerificationOfUserForThreeWeeksInARowWithNoDigitalDocumentSignature(
            referenceWeekDateTime,
            referenceWeekDateTime.plusWeeks(1),
            startDateTime,
            startDateTime.plusWeeks(1),
            startDateTime.plusWeeks(2),
            startDateTime.plusWeeks(3)
        );
    }

    public boolean hasVerifiedPinDuringWeek(Long userId, LocalDateTime startDateTime) {
        return userVerificationHistoryRepository
            .existsByUserIdAndOperationTypeAndVerificationTypeAndCreatedAtIsGreaterThanEqualAndCreatedAtIsLessThan(
                userId,
                VerificationOperationType.VERIFICATION,
                VerificationType.PIN,
                startDateTime,
                startDateTime.plusWeeks(1)
            );
    }

    public Optional<String> getCsvIndicatorByDigitalDocumentSignatureTypeDuring(
        Long userId,
        LocalDateTime startDateTime
    ) {
        return Optional.ofNullable(userVerificationHistoryRepository
            .getCsvIndicatorByUserIdAndDigitalDocumentSignatureDuring(
                userId,
                startDateTime,
                startDateTime.plusWeeks(1)
            ));
    }

    public long countNewRegisteredUserDuringWeekOfDigitalDocument(
        List<Long> digitalDocumentIds,
        LocalDateTime startWeekDateTime,
        LocalDateTime startWeekNextDateTime
    ) {
        return userVerificationHistoryRepository.countNewRegisteredUserBetweenByDigitalDocumentIds(
            digitalDocumentIds,
            startWeekDateTime,
            startWeekNextDateTime
        );
    }

    public long countSpecificDigitalDocumentCompletedAndPinVerificationUserWeekly(
        List<Long> digitalDocumentIds,
        LocalDateTime referenceWeekStartDateTime,
        LocalDateTime referenceWeekNextWeekDateTime,
        LocalDateTime startWeekDateTime
    ) {
        return userVerificationHistoryRepository.countPinVerificationOfUserBetweenAndSpecificDigitalDocumentCompleteAndRegisteredBetween(
            digitalDocumentIds,
            startWeekDateTime,
            startWeekDateTime.plusWeeks(1),
            referenceWeekStartDateTime,
            referenceWeekNextWeekDateTime
        );
    }

    public long countSpecificDigitalDocumentNotCompletedAndPinVerificationUserWeekly(
        List<Long> digitalDocumentIds,
        LocalDateTime referenceWeekStartDateTime,
        LocalDateTime referenceWeekNextWeekDateTime,
        LocalDateTime startWeekDateTime
    ) {
        return userVerificationHistoryRepository.countPinVerificationOfUserBetweenAndSpecificDigitalDocumentNotCompleteAndRegisterBetween(
            digitalDocumentIds,
            startWeekDateTime,
            startWeekDateTime.plusWeeks(1),
            referenceWeekStartDateTime,
            referenceWeekNextWeekDateTime
        );
    }

    public long countNewRegisteredUserNotCompleteSpecificDigitalDocument(
        List<Long> digitalDocumentIds,
        LocalDateTime startDateTime,
        LocalDateTime startDateNextWeekDateTime
    ) {
        return userVerificationHistoryRepository.countNewRegisteredUserBetweenAndNotCompletedSpecificDigitalDocument(
            startDateTime,
            startDateNextWeekDateTime,
            digitalDocumentIds
        );
    }
}
