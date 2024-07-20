package ag.act.service.download.datamatrix.data.provider;

import ag.act.dto.datamatrix.provider.UserRetentionDataByWeekProviderRequest;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import ag.act.service.user.UserVerificationHistoryService;
import ag.act.util.KoreanDateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Stream;

import static ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_GIVEN_REGISTER_AND_DIGITAL_DOCUMENT_COMPLETE;
import static ag.act.service.download.datamatrix.UserRetentionDataMapUtil.addValueToDataByWeek;
import static ag.act.service.download.datamatrix.UserRetentionDataMapUtil.initializeNumericDataMapByWeek;
import static ag.act.util.DateTimeUtil.getNextWeek;

@Slf4j
@Component
public class PinVerificationGivenRegisterAndDigitalDocumentCompleteByWeekProvider
    extends UserRetentionDataByWeekProvider<UserRetentionDataByWeekProviderRequest> {

    public PinVerificationGivenRegisterAndDigitalDocumentCompleteByWeekProvider(
        UserVerificationHistoryService userVerificationHistoryService
    ) {
        super(userVerificationHistoryService);
    }

    @Override
    public boolean supports(UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType) {
        return PIN_VERIFICATION_GIVEN_REGISTER_AND_DIGITAL_DOCUMENT_COMPLETE == userRetentionWeeklyCsvDataType;
    }

    @Override
    public Map<LocalDate, Double> getRetentionDataMap(UserRetentionDataByWeekProviderRequest userRetentionDataByWeekProviderRequest) {
        final var userRetentionDataMapPeriodDto = userRetentionDataByWeekProviderRequest.getUserRetentionDataMapPeriodDto();
        final LocalDate referenceWeek = userRetentionDataMapPeriodDto.getReferenceWeek();

        final LocalDate today = userRetentionDataMapPeriodDto.getEndDate();
        final LocalDate appRenewalDate = userRetentionDataMapPeriodDto.getStartDate();

        final Map<LocalDate, Double> userVerificationByWeek = initializeNumericDataMapByWeek(appRenewalDate, today);
        final LocalDateTime referenceWeekDateTime = KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDate(referenceWeek);

        final double userPinRegistrationDuringReferenceWeek = userVerificationHistoryService
            .countDigitalDocumentCompletedUserPinRegistrationBetween(referenceWeekDateTime, referenceWeekDateTime.plusWeeks(1));

        addValueToDataByWeek(
            referenceWeek,
            userPinRegistrationDuringReferenceWeek,
            userVerificationByWeek
        );

        Stream.iterate(
                getNextWeek(referenceWeek),
                week -> week.isBefore(today),
                week -> week.plusWeeks(1)
            )
            .parallel()
            .forEach(week -> {
                double activeUserCountDuringWeek = userVerificationHistoryService
                    .countDigitalDocumentCompletedUserPinVerificationWeekly(
                        referenceWeekDateTime,
                        KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDate(week)
                    );

                addValueToDataByWeek(
                    week,
                    activeUserCountDuringWeek,
                    userVerificationByWeek
                );
            });

        return userVerificationByWeek;
    }
}
