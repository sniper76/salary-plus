package ag.act.service.download.datamatrix.data.provider;

import ag.act.dto.SimpleUserDto;
import ag.act.dto.datamatrix.provider.UserRetentionDataByUserProviderRequest;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import ag.act.module.mydata.MyDataSummaryService;
import ag.act.service.user.UserVerificationHistoryService;
import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType.ALL_USERS_GIVEN_ALL_CONDITIONS;
import static ag.act.service.download.datamatrix.UserRetentionDataMapUtil.initializeStringDataMapByWeek;

@AllArgsConstructor
@Component
public class AllUsersGivenAllConditionsByWeekProvider
    extends UserRetentionDataByUserProvider<UserRetentionDataByUserProviderRequest> {

    private static final String MY_DATA = "1";
    private static final String PIN_VERIFICATION = "O";
    private static final String NO_VISIT = "X";
    private static final String BEFORE_REGISTER = "";

    private final MyDataSummaryService myDataSummaryService;
    private final UserVerificationHistoryService userVerificationHistoryService;

    @Override
    public boolean supports(UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType) {
        return ALL_USERS_GIVEN_ALL_CONDITIONS == userRetentionWeeklyCsvDataType;
    }

    public Map<LocalDate, String> getRetentionDataMap(
        UserRetentionDataByUserProviderRequest userRetentionDataByUserProviderRequest
    ) {
        final var userRetentionDataMapPeriodDto = userRetentionDataByUserProviderRequest.getUserRetentionDataMapPeriodDto();
        final LocalDate startDate = userRetentionDataMapPeriodDto.getStartDate();
        final LocalDate referenceDate = userRetentionDataMapPeriodDto.getEndDate();
        final SimpleUserDto user = userRetentionDataByUserProviderRequest.getSimpleUserDto();

        Map<LocalDate, String> userRetentionResults = initializeStringDataMapByWeek(startDate, referenceDate);

        final LocalDate userRegisteredDate = KoreanDateTimeUtil.toKoreanTime(user.getCreatedAt()).toLocalDate();

        Stream.iterate(
                startDate,
                week -> week.isBefore(referenceDate) || week.isEqual(referenceDate),
                week -> week.plusWeeks(1)
            )
            .parallel()
            .forEach(week -> {
                if (DateTimeUtil.getDateBeforeNextWeek(week).isBefore(userRegisteredDate)) {
                    userRetentionResults.put(week, BEFORE_REGISTER);
                    return;
                }

                String retentionData = getRetentionData(
                    user, KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDate(week)
                );

                userRetentionResults.put(week, retentionData);
            });

        return userRetentionResults;
    }

    private String getRetentionData(SimpleUserDto user, LocalDateTime startWeekDateTime) {

        final Optional<String> csvIndicator = userVerificationHistoryService
            .getCsvIndicatorByDigitalDocumentSignatureTypeDuring(user.getId(), startWeekDateTime);

        if (csvIndicator.isPresent()) {
            return csvIndicator.get(); // 위임장: a, 공동보유: b, 기타: c
        }
        if (hasCreatedMyDataDuringWeek(startWeekDateTime, user.getId())) {
            return MY_DATA;
        }
        if (hasVerifiedPinDuringWeek(startWeekDateTime, user.getId())) {
            return PIN_VERIFICATION;
        }
        return NO_VISIT;
    }

    private boolean hasCreatedMyDataDuringWeek(LocalDateTime startWeekDateTime, Long userId) {
        return myDataSummaryService.hasCreatedMyDataDuring(userId, startWeekDateTime);
    }

    private boolean hasVerifiedPinDuringWeek(LocalDateTime startWeekDateTime, Long userId) {
        return userVerificationHistoryService.hasVerifiedPinDuringWeek(userId, startWeekDateTime);
    }
}
