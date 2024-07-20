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

import static ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType.DIGITAL_DOCUMENT_PARTICIPATION_RATE_GIVEN_REGISTER;
import static ag.act.service.download.datamatrix.UserRetentionDataMapUtil.addValueToDataByWeek;
import static ag.act.service.download.datamatrix.UserRetentionDataMapUtil.initializeNumericDataMapByWeek;

@Component
public class DigitalDocumentParticipationRateGivenRegisterByWeekProvider
    extends UserRetentionDataByWeekProvider<UserRetentionDataByWeekProviderRequest> {

    public DigitalDocumentParticipationRateGivenRegisterByWeekProvider(
        UserVerificationHistoryService userVerificationHistoryService
    ) {
        super(userVerificationHistoryService);
    }

    @Override
    public boolean supports(UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType) {
        return DIGITAL_DOCUMENT_PARTICIPATION_RATE_GIVEN_REGISTER == userRetentionWeeklyCsvDataType;
    }

    @Override
    public Map<LocalDate, Double> getRetentionDataMap(UserRetentionDataByWeekProviderRequest userRetentionDataByWeekProviderRequest) {
        final var userRetentionDataMapPeriodDto = userRetentionDataByWeekProviderRequest.getUserRetentionDataMapPeriodDto();
        final LocalDate appRenewalDate = userRetentionDataMapPeriodDto.getStartDate();
        final LocalDate today = userRetentionDataMapPeriodDto.getEndDate();
        final LocalDate referenceWeek = userRetentionDataMapPeriodDto.getReferenceWeek();

        Map<LocalDate, Double> digitalDocumentParticipationRateByWeek = initializeNumericDataMapByWeek(appRenewalDate, today);

        final LocalDateTime referenceWeekDateTime = KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDate(referenceWeek);
        Stream.iterate(
                referenceWeek,
                week -> week.isBefore(today),
                week -> week.plusWeeks(1)
            )
            .parallel()
            .forEach(week -> {
                LocalDateTime startDateTime = KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDate(week);
                double activeUserCountDuringWeek = userVerificationHistoryService
                    .countMyDataConnectedUserPinVerificationWeekly(
                        referenceWeekDateTime,
                        startDateTime
                    );

                double participationCount = userVerificationHistoryService
                    .countDigitalDocumentParticipation(
                        referenceWeekDateTime,
                        startDateTime
                    );

                addValueToDataByWeek(
                    week,
                    activeUserCountDuringWeek == 0.0
                        ? 0.0
                        : participationCount / activeUserCountDuringWeek,
                    digitalDocumentParticipationRateByWeek
                );
            });

        return digitalDocumentParticipationRateByWeek;
    }
}
