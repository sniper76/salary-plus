package ag.act.service.download.datamatrix.data.provider;

import ag.act.dto.SimpleUserDto;
import ag.act.dto.datamatrix.provider.UserRetentionDataByUserProviderRequest;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import ag.act.service.admin.userholdingstockhistory.UserHoldingStockHistoryOnDateService;
import ag.act.util.KoreanDateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType.ALL_USERS_SIGNATURE_OPPORTUNITY_GIVEN_HAVE_STOCK_IN_ALL_DIGITAL_DOCUMENTS_PROGRESS_PERIOD;
import static ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType.ALL_USERS_SIGNATURE_OPPORTUNITY_GIVEN_HAVE_STOCK_IN_DIGITAL_DOCUMENTS_EXCEPT_ETC_PROGRESS_PERIOD;
import static ag.act.service.download.datamatrix.UserRetentionDataMapUtil.initializeStringDataMapByWeek;

@Component
@RequiredArgsConstructor
public class AllUsersSignatureOpportunityGivenHaveStockInAllDigitalDocumentsProgressPeriodByWeekProvider
    extends UserRetentionDataByUserProvider<UserRetentionDataByUserProviderRequest> {

    private final UserHoldingStockHistoryOnDateService userHoldingStockHistoryOnDateService;
    private final Map<UserRetentionWeeklyCsvDataType, List<String>> digitalDocumentTypeMap = Map.ofEntries(
        Map.entry(
            ALL_USERS_SIGNATURE_OPPORTUNITY_GIVEN_HAVE_STOCK_IN_ALL_DIGITAL_DOCUMENTS_PROGRESS_PERIOD,
            DigitalDocumentType.getNames()
        ),
        Map.entry(
            ALL_USERS_SIGNATURE_OPPORTUNITY_GIVEN_HAVE_STOCK_IN_DIGITAL_DOCUMENTS_EXCEPT_ETC_PROGRESS_PERIOD,
            DigitalDocumentType.getNamesExceptEtcDocument()
        )
    );

    @Override
    public boolean supports(UserRetentionWeeklyCsvDataType csvDataType) {
        return digitalDocumentTypeMap.containsKey(csvDataType);
    }

    public Map<LocalDate, String> getRetentionDataMap(
        UserRetentionDataByUserProviderRequest userRetentionDataByUserProviderRequest
    ) {
        final var userRetentionDataMapPeriodDto = userRetentionDataByUserProviderRequest.getUserRetentionDataMapPeriodDto();
        final LocalDate startDate = userRetentionDataMapPeriodDto.getStartDate();
        final LocalDate referenceDate = userRetentionDataMapPeriodDto.getEndDate();
        final SimpleUserDto user = userRetentionDataByUserProviderRequest.getSimpleUserDto();
        final UserRetentionWeeklyCsvDataType csvDataType = userRetentionDataByUserProviderRequest.getUserRetentionWeeklyCsvDataType();

        Map<LocalDate, String> userRetentionResults = initializeStringDataMapByWeek(startDate, referenceDate);

        Stream.iterate(
                startDate,
                week -> week.isBefore(referenceDate) || week.isEqual(referenceDate),
                week -> week.plusWeeks(1)
            )
            .parallel()
            .forEach(week -> {
                final String retentionData = getRetentionData(
                    user, week, KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDate(week), csvDataType
                );

                userRetentionResults.put(week, retentionData);
            });

        return userRetentionResults;
    }

    private String getRetentionData(
        SimpleUserDto user,
        LocalDate startDate,
        LocalDateTime startWeekDateTime,
        UserRetentionWeeklyCsvDataType csvDataType
    ) {
        return String.valueOf(
            userHoldingStockHistoryOnDateService.countDigitalDocumentSignatureOpportunity(
                user.getId(),
                digitalDocumentTypeMap.get(csvDataType),
                startWeekDateTime,
                startDate
            )
        );
    }
}
