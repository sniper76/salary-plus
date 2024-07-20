package ag.act.service.download.datamatrix.data.provider;

import ag.act.dto.datamatrix.provider.UserRetentionDataOfDigitalDocumentByWeekProviderRequest;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import ag.act.service.user.UserVerificationHistoryService;
import ag.act.util.KoreanDateTimeUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static ag.act.service.download.datamatrix.UserRetentionDataMapUtil.addValueToDataByWeek;
import static ag.act.service.download.datamatrix.UserRetentionDataMapUtil.initializeNumericDataMapByWeek;
import static ag.act.util.DateTimeUtil.getNextWeek;

@Component
public class PinVerificationGivenRegisterAndSpecificDigitalDocumentCompleteByWeekProvider
    extends UserRetentionDataByWeekProvider<UserRetentionDataOfDigitalDocumentByWeekProviderRequest> {

    public PinVerificationGivenRegisterAndSpecificDigitalDocumentCompleteByWeekProvider(
        UserVerificationHistoryService userVerificationHistoryService
    ) {
        super(userVerificationHistoryService);
    }

    @Override
    public boolean supports(UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType) {
        return userRetentionWeeklyCsvDataType
            == UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_GIVEN_REGISTER_AND_SPECIFIC_DIGITAL_DOCUMENT_COMPLETE;
    }

    public Map<LocalDate, Double> getRetentionDataMap(
        UserRetentionDataOfDigitalDocumentByWeekProviderRequest userRetentionDataByWeekProviderRequest
    ) {
        final var userRetentionDataMapPeriod = userRetentionDataByWeekProviderRequest.getUserRetentionDataMapPeriodDto();
        final LocalDate referenceWeek = userRetentionDataMapPeriod.getReferenceWeek();
        final LocalDateTime referenceWeekDateTime = KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDate(referenceWeek);

        final LocalDate retentionSearchStartDate = userRetentionDataMapPeriod.getStartDate();
        final LocalDate retentionSearchEndDate = userRetentionDataMapPeriod.getEndDate();
        final Map<LocalDate, Double> userVerificationByWeek = initializeNumericDataMapByWeek(retentionSearchStartDate, retentionSearchEndDate);

        final var digitalDocumentProgressPeriod = userRetentionDataByWeekProviderRequest.getDigitalDocumentProgressPeriodDto();
        final LocalDateTime digitalDocumentTargetStartDateTime = digitalDocumentProgressPeriod.getTargetStartDateTime();
        final LocalDateTime digitalDocumentTargetEndDateTime = digitalDocumentProgressPeriod.getTargetEndDateTime();

        final List<Long> digitalDocumentIds = userRetentionDataByWeekProviderRequest.getDigitalDocumentIds();

        long countNewRegisteredUserDuringWeek =
            getCountNewRegisteredUserDuringWeek(
                referenceWeekDateTime,
                digitalDocumentTargetStartDateTime,
                digitalDocumentTargetEndDateTime,
                digitalDocumentIds
            );

        addValueToDataByWeek(
            referenceWeek,
            countNewRegisteredUserDuringWeek,
            userVerificationByWeek
        );

        final LocalDate referenceNextWeek = getNextWeek(referenceWeek);
        Stream.iterate(
                referenceNextWeek,
                week -> week.isBefore(retentionSearchEndDate),
                week -> week.plusWeeks(1)
            )
            .parallel()
            .forEach(week -> {
                long countUserRetentionDuringWeek =
                    getCountUserRetentionDuringWeek(
                        digitalDocumentIds,
                        referenceWeekDateTime,
                        digitalDocumentTargetStartDateTime,
                        digitalDocumentTargetEndDateTime,
                        KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDate(week)
                    );

                addValueToDataByWeek(
                    week,
                    countUserRetentionDuringWeek,
                    userVerificationByWeek
                );
            });

        return userVerificationByWeek;
    }

    private long getCountNewRegisteredUserDuringWeek(
        LocalDateTime referenceWeekDateTime,
        LocalDateTime digitalDocumentStartDateTime,
        LocalDateTime digitalDocumentEndDateTime,
        List<Long> digitalDocumentIds
    ) {
        final LocalDateTime referenceWeekNextDateTime = referenceWeekDateTime.plusWeeks(1);
        if (referenceWeekDateTime.isBefore(digitalDocumentStartDateTime)) {
            return userVerificationHistoryService.countNewRegisteredUserDuringWeekOfDigitalDocument(
                digitalDocumentIds,
                digitalDocumentStartDateTime,
                referenceWeekNextDateTime
            );
        }

        if (referenceWeekNextDateTime.isAfter(digitalDocumentEndDateTime)) {
            return userVerificationHistoryService.countNewRegisteredUserDuringWeekOfDigitalDocument(
                digitalDocumentIds,
                referenceWeekDateTime,
                digitalDocumentEndDateTime
            );
        }

        return userVerificationHistoryService.countNewRegisteredUserDuringWeekOfDigitalDocument(
            digitalDocumentIds,
            referenceWeekDateTime,
            referenceWeekNextDateTime
        );
    }

    private long getCountUserRetentionDuringWeek(
        List<Long> digitalDocumentIds,
        LocalDateTime referenceStartWeekDateTime,
        LocalDateTime digitalDocumentStartDateTime,
        LocalDateTime digitalDocumentEndDateTime,
        LocalDateTime startDateTime
    ) {
        final LocalDateTime referenceWeekNextWeekDateTime = referenceStartWeekDateTime.plusWeeks(1);

        if (referenceStartWeekDateTime.isBefore(digitalDocumentStartDateTime)) {
            return userVerificationHistoryService.countSpecificDigitalDocumentCompletedAndPinVerificationUserWeekly(
                digitalDocumentIds,
                digitalDocumentStartDateTime,
                referenceWeekNextWeekDateTime,
                startDateTime
            );
        }

        if (referenceWeekNextWeekDateTime.isAfter(digitalDocumentEndDateTime)) {
            return userVerificationHistoryService.countSpecificDigitalDocumentCompletedAndPinVerificationUserWeekly(
                digitalDocumentIds,
                referenceStartWeekDateTime,
                digitalDocumentEndDateTime,
                startDateTime
            );
        }

        return userVerificationHistoryService.countSpecificDigitalDocumentCompletedAndPinVerificationUserWeekly(
            digitalDocumentIds,
            referenceStartWeekDateTime,
            referenceWeekNextWeekDateTime,
            startDateTime
        );
    }
}
