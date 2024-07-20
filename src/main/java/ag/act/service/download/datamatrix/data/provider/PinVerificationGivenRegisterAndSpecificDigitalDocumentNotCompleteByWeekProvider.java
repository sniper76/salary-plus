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
public class PinVerificationGivenRegisterAndSpecificDigitalDocumentNotCompleteByWeekProvider
    extends UserRetentionDataByWeekProvider<UserRetentionDataOfDigitalDocumentByWeekProviderRequest> {

    public PinVerificationGivenRegisterAndSpecificDigitalDocumentNotCompleteByWeekProvider(
        UserVerificationHistoryService userVerificationHistoryService
    ) {
        super(userVerificationHistoryService);
    }

    @Override
    public boolean supports(UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType) {
        return userRetentionWeeklyCsvDataType
            == UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_GIVEN_REGISTER_AND_SPECIFIC_DIGITAL_DOCUMENT_NOT_COMPLETE;
    }

    @Override
    public Map<LocalDate, Double> getRetentionDataMap(
        UserRetentionDataOfDigitalDocumentByWeekProviderRequest userRetentionDataByWeekProviderRequest
    ) {
        final var retentionDataMapPeriodDto = userRetentionDataByWeekProviderRequest.getUserRetentionDataMapPeriodDto();

        final LocalDate referenceWeek = userRetentionDataByWeekProviderRequest.getUserRetentionDataMapPeriodDto().getReferenceWeek();
        LocalDateTime referenceWeekDateTime = KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDate(referenceWeek);

        final LocalDate referenceSearchStartDate = retentionDataMapPeriodDto.getStartDate();
        final LocalDate today = retentionDataMapPeriodDto.getEndDate();

        Map<LocalDate, Double> userVerificationDataMap = initializeNumericDataMapByWeek(referenceSearchStartDate, today);

        final var digitalDocumentProgressPeriod = userRetentionDataByWeekProviderRequest.getDigitalDocumentProgressPeriodDto();
        final LocalDateTime digitalDocumentStartDateTime = digitalDocumentProgressPeriod.getTargetStartDateTime();
        final LocalDateTime digitalDocumentEndDateTime = digitalDocumentProgressPeriod.getTargetEndDateTime();

        final List<Long> digitalDocumentIds = userRetentionDataByWeekProviderRequest.getDigitalDocumentIds();

        final long newRegisteredUserCount = getCountNewRegisteredUserDuringWeek(
            referenceWeekDateTime,
            digitalDocumentStartDateTime,
            digitalDocumentEndDateTime,
            digitalDocumentIds
        );

        addValueToDataByWeek(referenceWeek, newRegisteredUserCount, userVerificationDataMap);

        final LocalDate referenceNextWeek = getNextWeek(referenceWeek);
        Stream.iterate(
                referenceNextWeek,
                week -> week.isBefore(today),
                week -> week.plusWeeks(1)
            ).parallel()
            .forEach(week -> {
                long countUserRetentionDuringWeek =
                    getCountUserRetentionDuringWeek(
                        digitalDocumentIds,
                        referenceWeekDateTime,
                        digitalDocumentStartDateTime,
                        digitalDocumentEndDateTime,
                        KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDate(week)
                    );

                addValueToDataByWeek(
                    week,
                    countUserRetentionDuringWeek,
                    userVerificationDataMap
                );
            });

        return userVerificationDataMap;
    }

    private long getCountNewRegisteredUserDuringWeek(
        LocalDateTime referenceWeekDateTime,
        LocalDateTime digitalDocumentStartDateTime,
        LocalDateTime digitalDocumentEndDateTime,
        List<Long> digitalDocumentIds
    ) {
        LocalDateTime referenceNextWeekDateTime = referenceWeekDateTime.plusWeeks(1);
        if (referenceWeekDateTime.isBefore(digitalDocumentStartDateTime)) {
            return userVerificationHistoryService.countNewRegisteredUserNotCompleteSpecificDigitalDocument(
                digitalDocumentIds,
                digitalDocumentStartDateTime,
                referenceNextWeekDateTime
            );
        }

        if (referenceNextWeekDateTime.isAfter(digitalDocumentEndDateTime)) {
            return userVerificationHistoryService.countNewRegisteredUserNotCompleteSpecificDigitalDocument(
                digitalDocumentIds,
                referenceWeekDateTime,
                digitalDocumentEndDateTime
            );
        }

        return userVerificationHistoryService.countNewRegisteredUserNotCompleteSpecificDigitalDocument(
            digitalDocumentIds,
            referenceWeekDateTime,
            referenceNextWeekDateTime
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
            return userVerificationHistoryService.countSpecificDigitalDocumentNotCompletedAndPinVerificationUserWeekly(
                digitalDocumentIds,
                digitalDocumentStartDateTime,
                referenceWeekNextWeekDateTime,
                startDateTime
            );
        }

        if (referenceWeekNextWeekDateTime.isAfter(digitalDocumentEndDateTime)) {
            return userVerificationHistoryService.countSpecificDigitalDocumentNotCompletedAndPinVerificationUserWeekly(
                digitalDocumentIds,
                referenceStartWeekDateTime,
                digitalDocumentEndDateTime,
                startDateTime
            );
        }

        return userVerificationHistoryService.countSpecificDigitalDocumentNotCompletedAndPinVerificationUserWeekly(
            digitalDocumentIds,
            referenceStartWeekDateTime,
            referenceWeekNextWeekDateTime,
            startDateTime
        );
    }
}
