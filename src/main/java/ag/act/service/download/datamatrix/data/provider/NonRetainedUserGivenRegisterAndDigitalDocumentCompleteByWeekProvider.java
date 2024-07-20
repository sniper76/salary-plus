package ag.act.service.download.datamatrix.data.provider;

import ag.act.dto.datamatrix.provider.UserRetentionDataByWeekProviderRequest;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import ag.act.service.user.UserVerificationHistoryService;
import ag.act.util.KoreanDateTimeUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Stream;

import static ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType.NON_RETAINED_USER_GIVEN_REGISTER_AND_DIGITAL_DOCUMENT_COMPLETE;
import static ag.act.service.download.datamatrix.UserRetentionDataMapUtil.addValueToDataByWeek;
import static ag.act.service.download.datamatrix.UserRetentionDataMapUtil.initializeNumericDataMapByWeek;
import static ag.act.util.DateTimeUtil.getNextWeek;

@Component
public class NonRetainedUserGivenRegisterAndDigitalDocumentCompleteByWeekProvider
    extends UserRetentionDataByWeekProvider<UserRetentionDataByWeekProviderRequest> {
    public NonRetainedUserGivenRegisterAndDigitalDocumentCompleteByWeekProvider(
        UserVerificationHistoryService userVerificationHistoryService
    ) {
        super(userVerificationHistoryService);
    }

    @Override
    public boolean supports(
        UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType
    ) {
        return NON_RETAINED_USER_GIVEN_REGISTER_AND_DIGITAL_DOCUMENT_COMPLETE == userRetentionWeeklyCsvDataType;
    }

    @Override
    public Map<LocalDate, Double> getRetentionDataMap(UserRetentionDataByWeekProviderRequest userRetentionDataByWeekProviderRequest) {
        final var userRetentionDataMapPeriodDto = userRetentionDataByWeekProviderRequest.getUserRetentionDataMapPeriodDto();
        final LocalDate appRenewalDate = userRetentionDataMapPeriodDto.getStartDate();
        final LocalDate today = userRetentionDataMapPeriodDto.getEndDate();
        final LocalDate referenceWeek = userRetentionDataMapPeriodDto.getReferenceWeek();

        final LocalDate referenceWeekNextWeek = getNextWeek(referenceWeek);
        final LocalDateTime referenceWeekDateTime = KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDate(referenceWeek);

        final Map<LocalDate, Double> userVerificationByWeek = initializeNumericDataMapByWeek(appRenewalDate, today);

        final double nonRetainedUserCountDuringReferenceWeek = userVerificationHistoryService
            .countDigitalDocumentCompletedUserPinRegistrationBetween(referenceWeekDateTime, referenceWeekDateTime.plusWeeks(1));

        addValueToDataByWeek(
            referenceWeek,
            nonRetainedUserCountDuringReferenceWeek,
            userVerificationByWeek
        );

        Stream.iterate(
                referenceWeekNextWeek,
                week -> week.isBefore(today),
                week -> week.plusWeeks(1)
            )
            .parallel()
            .forEach(week -> {
                double nonRetainedUserCountDuringWeek = userVerificationHistoryService
                    .countDigitalDocumentCompletedAndNonRetainedUserWeekly(
                        referenceWeekDateTime,
                        KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDate(week)
                    );

                addValueToDataByWeek(
                    week,
                    nonRetainedUserCountDuringWeek,
                    userVerificationByWeek
                );
            });

        return userVerificationByWeek;
    }
}
