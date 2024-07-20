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

import static ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_GIVEN_FIRST_DIGITAL_DOCUMENT_COMPLETE;
import static ag.act.service.download.datamatrix.UserRetentionDataMapUtil.addValueToDataByWeek;
import static ag.act.service.download.datamatrix.UserRetentionDataMapUtil.initializeNumericDataMapByWeek;
import static ag.act.util.DateTimeUtil.getNextWeek;

@Component
public class PinVerificationGivenFirstDigitalDocumentCompleteByWeekProvider
    extends UserRetentionDataByWeekProvider<UserRetentionDataByWeekProviderRequest> {

    public PinVerificationGivenFirstDigitalDocumentCompleteByWeekProvider(
        UserVerificationHistoryService userVerificationHistoryService
    ) {
        super(userVerificationHistoryService);
    }

    @Override
    public boolean supports(UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType) {
        return PIN_VERIFICATION_GIVEN_FIRST_DIGITAL_DOCUMENT_COMPLETE == userRetentionWeeklyCsvDataType;
    }

    @Override
    public Map<LocalDate, Double> getRetentionDataMap(UserRetentionDataByWeekProviderRequest userRetentionDataByWeekProviderRequest) {
        final var userRetentionDataMapPeriodDto = userRetentionDataByWeekProviderRequest.getUserRetentionDataMapPeriodDto();
        final LocalDate appRenewalDate = userRetentionDataMapPeriodDto.getStartDate();
        final LocalDate today = userRetentionDataMapPeriodDto.getEndDate();
        final LocalDate referenceWeek = userRetentionDataMapPeriodDto.getReferenceWeek();

        final Map<LocalDate, Double> retentionAfterDigitalDocumentCompleteByWeek = initializeNumericDataMapByWeek(appRenewalDate, today);
        final LocalDateTime referenceWeekDateTime = KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDate(referenceWeek);

        final double firstDigitalDocumentSaveCountDuringReferenceWeek =
            userVerificationHistoryService.countFirstDigitalDocumentSaveDuring(referenceWeekDateTime);

        addValueToDataByWeek(
            referenceWeek,
            firstDigitalDocumentSaveCountDuringReferenceWeek,
            retentionAfterDigitalDocumentCompleteByWeek
        );

        Stream.iterate(
                getNextWeek(referenceWeek),
                week -> week.isBefore(today),
                week -> week.plusWeeks(1)
            )
            .parallel()
            .forEach(week -> {
                double activeUserCountDuringWeek = userVerificationHistoryService
                    .countPinVerificationWeeklyGivenFirstDigitalDocumentSave(
                        referenceWeekDateTime,
                        KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDate(week)
                    );

                addValueToDataByWeek(
                    week,
                    activeUserCountDuringWeek,
                    retentionAfterDigitalDocumentCompleteByWeek
                );
            });

        return retentionAfterDigitalDocumentCompleteByWeek;
    }
}
