package ag.act.service.download.datamatrix.data.provider;

import ag.act.dto.datamatrix.provider.UserRetentionDataByWeekProviderRequest;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import ag.act.service.user.UserVerificationHistoryService;
import ag.act.util.KoreanDateTimeUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Stream;

import static ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_FOR_THREE_WEEKS_IN_A_ROW_GIVEN_REGISTER_AND_DIGITAL_DOCUMENT_COMPLETE;
import static ag.act.service.download.datamatrix.UserRetentionDataMapUtil.addValueToDataByWeek;
import static ag.act.service.download.datamatrix.UserRetentionDataMapUtil.initializeNumericDataMapByWeek;

@Component
public class PinVerificationForThreeWeeksInARowGivenRegisterAndDigitalDocumentCompleteByWeekProvider
    extends UserRetentionDataByWeekProvider<UserRetentionDataByWeekProviderRequest> {

    public PinVerificationForThreeWeeksInARowGivenRegisterAndDigitalDocumentCompleteByWeekProvider(
        UserVerificationHistoryService userVerificationHistoryService
    ) {
        super(userVerificationHistoryService);
    }

    @Override
    public boolean supports(UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType) {
        return PIN_VERIFICATION_FOR_THREE_WEEKS_IN_A_ROW_GIVEN_REGISTER_AND_DIGITAL_DOCUMENT_COMPLETE
            == userRetentionWeeklyCsvDataType;
    }

    @Override
    public Map<LocalDate, Double> getRetentionDataMap(UserRetentionDataByWeekProviderRequest userRetentionDataByWeekProviderRequest) {
        final var userRetentionDataMapPeriodDto = userRetentionDataByWeekProviderRequest.getUserRetentionDataMapPeriodDto();
        final LocalDate appRenewalDate = userRetentionDataMapPeriodDto.getStartDate();
        final LocalDate referenceWeek = userRetentionDataMapPeriodDto.getReferenceWeek();
        final LocalDate today = userRetentionDataMapPeriodDto.getEndDate();

        final Map<LocalDate, Double> pinVerificationByWeek = initializeNumericDataMapByWeek(appRenewalDate, today);

        Stream.iterate(
                referenceWeek,
                week -> week.isBefore(today),
                week -> week.plusWeeks(1)
            )
            .parallel()
            .forEach(week -> {
                double activeUserCountDuringThreeWeeks = userVerificationHistoryService
                    .countDigitalDocumentCompletedAndPinVerificationUserForThreeWeeksInARow(
                        KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDate(referenceWeek),
                        KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDate(week)
                    );

                addValueToDataByWeek(
                    week,
                    activeUserCountDuringThreeWeeks,
                    pinVerificationByWeek
                );
            });

        return pinVerificationByWeek;
    }
}
