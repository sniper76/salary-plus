package ag.act.module.digitaldocumentgenerator.converter;

import ag.act.entity.User;
import ag.act.entity.UserVerificationHistory;
import ag.act.enums.ProgressRecordFillType;
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import ag.act.exception.NotFoundException;
import ag.act.module.digitaldocumentgenerator.model.ProgressRecordFill;
import ag.act.service.user.UserService;
import ag.act.service.user.UserVerificationHistoryService;
import ag.act.util.DateTimeFormatUtil;
import ag.act.util.KoreanDateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ProgressRecordFillConverter {
    private final UserService userService;
    private final UserVerificationHistoryService userVerificationHistoryService;

    public List<ProgressRecordFill> convert(Long digitalDocumentUserId, Long currentUserId) {
        User user = userService.getUser(currentUserId);

        return Arrays.stream(ProgressRecordFillType.values())
            .sorted(Comparator.comparing(ProgressRecordFillType::getOrder))
            .map(type -> createProgressRecordFill(digitalDocumentUserId, user, type))
            .toList();
    }

    private ProgressRecordFill createProgressRecordFill(
        Long digitalDocumentUserId, User user, ProgressRecordFillType type
    ) {
        UserVerificationHistory history =
            getUserVerificationHistory(user.getId(), digitalDocumentUserId, type)
                .orElseThrow(() ->
                    new NotFoundException("%s번 유저의 %s를 활용한 %s 기록을 찾을 수 없습니다.".formatted(
                        user.getId(), type.getVerificationType().name(), type.getOperationType().getValue())
                    )
                );

        return generateRecordFill(user, history, type.getFormat());
    }

    private Optional<UserVerificationHistory> getUserVerificationHistory(
        Long userId, Long digitalDocumentUserId, ProgressRecordFillType type
    ) {
        final VerificationType verificationType = type.getVerificationType();
        final VerificationOperationType operationType = type.getOperationType();

        // 최초 SMS 인증 히스토리 조회인 경우
        if (ProgressRecordFillType.SMS_VERIFICATION == type) {
            return userVerificationHistoryService
                .findFirstVerificationHistory(userId, verificationType, operationType);
        }

        if (type.isDocumentSpecific()) {
            return userVerificationHistoryService.findLatestDigitalDocumentSpecificHistory(
                userId,
                verificationType,
                operationType,
                digitalDocumentUserId
            );
        }

        return userVerificationHistoryService.findLatestHistory(userId, verificationType, operationType);
    }

    private ProgressRecordFill generateRecordFill(User user, UserVerificationHistory history, String format) {
        return ProgressRecordFill.builder()
            .time(generateTimeString(history.getCreatedAt()))
            .message(format.formatted(generateUserNameAndBirthDateString(user)))
            .ipAddress(history.getUserIp())
            .build();
    }

    private String generateTimeString(LocalDateTime time) {
        return DateTimeFormatUtil.yyyy_MM_dd_hh_mm_ss_a().format(
            KoreanDateTimeUtil.toKoreanTime(time)
        );
    }

    private String generateUserNameAndBirthDateString(User user) {
        return "%s(%s)".formatted(user.getName(), DateTimeFormatUtil.yyMMdd().format(user.getBirthDate().toLocalDate()));
    }
}
