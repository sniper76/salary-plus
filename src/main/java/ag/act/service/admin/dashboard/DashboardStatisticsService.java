package ag.act.service.admin.dashboard;


import ag.act.converter.dashboard.DashboardStatisticsResponseConverter;
import ag.act.dto.admin.DashboardSearchParamDto;
import ag.act.dto.admin.DashboardStatisticsCountDto;
import ag.act.dto.admin.DashboardStatisticsParamDto;
import ag.act.entity.admin.DashboardStatistics;
import ag.act.enums.admin.DashboardStatisticsPeriodType;
import ag.act.enums.admin.DashboardStatisticsType;
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import ag.act.model.DashboardStatisticsResponse;
import ag.act.model.Status;
import ag.act.repository.PostUserViewRepository;
import ag.act.repository.SolidarityDailyStatisticsRepository;
import ag.act.repository.UserRepository;
import ag.act.repository.UserVerificationHistoryRepository;
import ag.act.repository.admin.DashboardStatisticsRepository;
import ag.act.util.DashboardStatisticsEnumsUtil;
import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DashboardStatisticsService {
    private final UserRepository userRepository;
    private final PostUserViewRepository postUserViewRepository;
    private final DashboardStatisticsRepository dashboardStatisticsRepository;
    private final UserVerificationHistoryRepository userVerificationHistoryRepository;
    private final DashboardStatisticsResponseConverter dashboardStatisticsResponseConverter;
    private final SolidarityDailyStatisticsRepository solidarityDailyStatisticsRepository;

    public void saveUserActiveMyDataAccessCount(DashboardStatisticsType type) {
        saveDashboardStatistics(
            type,
            () -> userRepository.countByUserActiveMyDataAccessCount(
                    KoreanDateTimeUtil.getYesterdayStartDateTime(),
                    KoreanDateTimeUtil.getYesterdayEndDateTime()
                )
                .map(Long::doubleValue)
                .orElse(0.0)
        );
    }

    public void saveUserActiveMyDataAccessAccumulate(DashboardStatisticsType type) {
        saveDashboardStatistics(
            type,
            () -> userRepository.countByUserActiveMyDataAccessAccumulate(
                    KoreanDateTimeUtil.getYesterdayEndDateTime()
                )
                .map(Long::doubleValue)
                .orElse(0.0)
        );
    }

    public void saveUserStatusProcessingCount(DashboardStatisticsType type) {
        saveDashboardStatistics(
            type,
            () -> userRepository.countByStatusInAndCreatedAtBetween(
                    List.of(Status.PROCESSING),
                    KoreanDateTimeUtil.getYesterdayStartDateTime(),
                    KoreanDateTimeUtil.getYesterdayEndDateTime()
                )
                .map(Long::doubleValue)
                .orElse(0.0)
        );
    }

    public void saveUserStatusActiveCount(DashboardStatisticsType type) {
        saveDashboardStatistics(
            type,
            () -> userRepository.countByStatusInAndUpdatedAtBetween(
                    List.of(Status.ACTIVE),
                    KoreanDateTimeUtil.getYesterdayStartDateTime(),
                    KoreanDateTimeUtil.getYesterdayEndDateTime()
                )
                .map(Long::doubleValue)
                .orElse(0.0)
        );
    }

    public void saveUserStatusWithdrawalCount(DashboardStatisticsType type) {
        saveDashboardStatistics(
            type,
            () -> userRepository.countByStatusInAndUpdatedAtBetween(
                    List.of(Status.WITHDRAWAL_REQUESTED),
                    KoreanDateTimeUtil.getYesterdayStartDateTime(),
                    KoreanDateTimeUtil.getYesterdayEndDateTime()
                )
                .map(Long::doubleValue)
                .orElse(0.0)
        );
    }

    public void saveUserRegistrationCount(DashboardStatisticsType type) {
        saveDashboardStatistics(
            type,
            () -> userVerificationHistoryRepository.countByVerificationTypeAndOperationTypeAndCreatedAtBetween(
                    VerificationType.USER, VerificationOperationType.REGISTER,
                    KoreanDateTimeUtil.getYesterdayStartDateTime(),
                    KoreanDateTimeUtil.getYesterdayEndDateTime()
                )
                .map(Long::doubleValue)
                .orElse(0.0)
        );
    }

    public void saveUserWithdrawalCount(DashboardStatisticsType type) {
        saveDashboardStatistics(
            type,
            () -> userRepository.countByStatusInAndUpdatedAtBetween(
                    DashboardStatisticsEnumsUtil.getUserWithdrawalStatuses(),
                    KoreanDateTimeUtil.getYesterdayStartDateTime(),
                    KoreanDateTimeUtil.getYesterdayEndDateTime()
                )
                .map(Long::doubleValue)
                .orElse(0.0)
        );
    }

    public void saveTotalPostViewCount(DashboardStatisticsType type) {
        saveDashboardStatistics(
            type,
            () -> postUserViewRepository.sumViewCount(
                    KoreanDateTimeUtil.getYesterdayStartDateTime(),
                    KoreanDateTimeUtil.getYesterdayEndDateTime()
                )
                .map(Long::doubleValue)
                .orElse(0.0)
        );
    }

    public void saveUserLoginCount(DashboardStatisticsType type) {
        saveDashboardStatistics(
            type,
            () -> userVerificationHistoryRepository.countByVerificationTypeAndOperationTypeAndCreatedAtBetween(
                    VerificationType.PIN, VerificationOperationType.VERIFICATION,
                    KoreanDateTimeUtil.getYesterdayStartDateTime(),
                    KoreanDateTimeUtil.getYesterdayEndDateTime()
                )
                .map(Long::doubleValue)
                .orElse(0.0)
        );
    }

    public void saveDailyActiveUser(DashboardStatisticsType type) {
        saveDashboardStatistics(
            type,
            () -> userVerificationHistoryRepository.countByActiveUser(
                    KoreanDateTimeUtil.getYesterdayStartDateTime(),
                    KoreanDateTimeUtil.getYesterdayEndDateTime()
                )
                .map(Long::doubleValue)
                .orElse(0.0)
        );
    }

    public void saveMonthlyActiveUser(DashboardStatisticsType type) {
        saveDashboardStatistics(
            type,
            DashboardStatisticsPeriodType.MONTHLY,
            () -> userVerificationHistoryRepository.countByActiveUser(
                    KoreanDateTimeUtil.getStartDateTimeOfThisMonthFromYesterday(),
                    KoreanDateTimeUtil.getEndDateTimeOfThisMonthFromYesterday()
                )
                .map(Long::doubleValue)
                .orElse(0.0)
        );
    }

    public void saveUserAverageLogin(DashboardStatisticsType type) {
        saveDashboardStatistics(
            type,
            () -> userVerificationHistoryRepository.findAverageLoginCountByUser(
                KoreanDateTimeUtil.getYesterdayStartDateTime(),
                KoreanDateTimeUtil.getYesterdayEndDateTime()
            )
        );
    }

    public void saveReuseAverageLogin(DashboardStatisticsType type) {
        saveDashboardStatistics(
            type,
            () -> userVerificationHistoryRepository.findReuseAverageByUser(
                KoreanDateTimeUtil.getYesterdayStartDateTime(),
                KoreanDateTimeUtil.getYesterdayEndDateTime()
            )
        );
    }

    public void saveTotalAsset(DashboardStatisticsType type) {
        saveDashboardStatistics(
            type,
            () -> solidarityDailyStatisticsRepository.sumMarketValueAndDate(
                KoreanDateTimeUtil.getYesterdayEndDateTime().toLocalDate()
            )
        );
    }

    private void saveDashboardStatistics(
        DashboardStatisticsType type,
        Supplier<Double> supplier
    ) {
        saveDashboardStatistics(
            type,
            DashboardStatisticsPeriodType.DAILY,
            supplier
        );
    }

    private void saveDashboardStatistics(
        DashboardStatisticsType type,
        DashboardStatisticsPeriodType periodType,
        Supplier<Double> supplier
    ) {
        Double average = supplier.get();

        String date = getDate(periodType.getPeriodFormat());
        saveDashboardStatistics(average, type, date);
        setLogMessage(type, average, date);
    }

    private void saveDashboardStatistics(Double count, DashboardStatisticsType type, String date) {
        DashboardStatistics dashboardStatistics = dashboardStatisticsRepository.findByTypeAndDate(type, date)
            .orElse(new DashboardStatistics());

        dashboardStatistics.setType(type);
        dashboardStatistics.setDate(date);
        dashboardStatistics.setValue(count);

        dashboardStatisticsRepository.save(dashboardStatistics);
    }

    private void setLogMessage(DashboardStatisticsType type, Object data, String date) {
        log.info("[DashboardStatistics] %s batch successfully finished. [data: %s] on %s".formatted(
            type.getDisplayName(), data, date
        ));
    }

    private String getDate(String format) {
        return KoreanDateTimeUtil.getFormattedYesterdayTime(format);
    }

    public List<DashboardStatisticsResponse> getStatistics(DashboardStatisticsParamDto paramDto) {
        Map<DashboardStatisticsType, List<DashboardStatisticsCountDto>> statisticsMap = getDashboardStatisticsTypeListMap(paramDto);

        return statisticsMap.entrySet()
            .stream()
            .sorted(Comparator.comparing(entry -> entry.getKey().getDisplayOrder()))
            .map(entry -> dashboardStatisticsResponseConverter.convert(paramDto, entry.getKey(), entry.getValue()))
            .toList();
    }

    private Map<DashboardStatisticsType, List<DashboardStatisticsCountDto>> getDashboardStatisticsTypeListMap(
        DashboardStatisticsParamDto paramDto
    ) {
        if (DashboardStatisticsPeriodType.MONTHLY == paramDto.getPeriodType()) {
            return mapDashboardStatisticsListByType(getMonthlyDashboardStatisticsCountDtoList(paramDto));
        }
        return mapDashboardStatisticsListByType(getDailyDashboardStatisticsCountDtoList(paramDto));
    }

    private List<DashboardStatisticsCountDto> getDailyDashboardStatisticsCountDtoList(
        DashboardStatisticsParamDto paramDto
    ) {
        return Stream.concat(
            getDailyByTypeInAndDateBetweenOrderByDate(paramDto.getDashboardSearchParamDtoForDailyExceptTotalAssetPrice()).stream(),
            getDailyByTypeInAndDateInOrderByDate(paramDto.getDashboardSearchParamDtoForDailyTotalAssetPrice()).stream()
        ).toList();
    }

    private List<DashboardStatisticsCountDto> getDailyByTypeInAndDateInOrderByDate(
        DashboardSearchParamDto dailyStoredSearchParamDto
    ) {
        return dashboardStatisticsRepository.findAllDailyByTypeInAndDateInOrderByDate(
            dailyStoredSearchParamDto.getDashboardStatisticsTypeList(),
            dailyStoredSearchParamDto.getDateList()
        );
    }

    private List<DashboardStatisticsCountDto> getDailyByTypeInAndDateBetweenOrderByDate(
        DashboardSearchParamDto dailyStoredSearchParamDto
    ) {
        return dashboardStatisticsRepository.findAllDailyByTypeInAndDateBetweenOrderByDate(
            dailyStoredSearchParamDto.getDashboardStatisticsTypeList(),
            dailyStoredSearchParamDto.getStartPeriod(),
            dailyStoredSearchParamDto.getEndPeriod()
        );
    }

    private List<DashboardStatisticsCountDto> getMonthlyDashboardStatisticsCountDtoList(DashboardStatisticsParamDto paramDto) {
        return Stream.of(
                getMonthlyByTypeInAndDateBetweenOrderByDate(paramDto.getDashboardSearchParamDtoForMonthlySumStatistics()).stream(),
                getDailyByTypeInAndDateInOrderByDate(paramDto.getDashboardSearchParamDtoForMonthlyTotalAssetPrice()).stream()
                    .map(it -> new DashboardStatisticsCountDto(it.getType(), DateTimeUtil.extractYearMonth(it.getDate()), it.getValue())),
                getDailyByTypeInAndDateInOrderByDate(paramDto.getDashboardSearchParamDtoForMonthlyActiveUser()).stream()
            )
            .flatMap(Function.identity())
            .toList();
    }

    private List<DashboardStatisticsCountDto> getMonthlyByTypeInAndDateBetweenOrderByDate(
        DashboardSearchParamDto dashboardSearchParamDto
    ) {
        return dashboardStatisticsRepository.findAllMonthlyByTypeInAndDateBetweenOrderByDate(
            dashboardSearchParamDto.getDashboardStatisticsTypeList(),
            dashboardSearchParamDto.getStartPeriod(),
            dashboardSearchParamDto.getEndPeriod()
        );
    }

    private Map<DashboardStatisticsType, List<DashboardStatisticsCountDto>> mapDashboardStatisticsListByType(
        List<DashboardStatisticsCountDto> statisticsList
    ) {
        return statisticsList.stream()
            .collect(Collectors.groupingBy(DashboardStatisticsCountDto::getType));
    }
}
