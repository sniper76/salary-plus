package ag.act.service.admin.dashboard;


import ag.act.converter.dashboard.DashboardStatisticsResponseConverter;
import ag.act.dto.admin.DashboardSearchParamDto;
import ag.act.dto.admin.DashboardStatisticsCountDto;
import ag.act.dto.admin.DashboardStatisticsParamDto;
import ag.act.entity.admin.DashboardStockStatistics;
import ag.act.enums.admin.DashboardStatisticsPeriodType;
import ag.act.enums.admin.DashboardStatisticsType;
import ag.act.module.dashboard.statistics.ICountItem;
import ag.act.repository.PostRepository;
import ag.act.repository.UserHoldingStockRepository;
import ag.act.repository.admin.DashboardStockStatisticsRepository;
import ag.act.util.KoreanDateTimeUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DashboardStockStatisticsService {
    private static final double DEFAULT_COUNT = 0;
    private static final int ADD_COUNT = 1;
    private final DashboardStockStatisticsRepository dashboardStockStatisticsRepository;
    private final UserHoldingStockRepository userHoldingStockRepository;
    private final PostRepository postRepository;
    private final DashboardStatisticsResponseConverter dashboardStatisticsResponseConverter;

    public void saveDailyUserRegistrationAverageRate(DashboardStatisticsType type) {
        saveDashboardStockStatistics(type, userHoldingStockRepository::findAllStockUserCount);
    }

    public void saveDailyAssetAverageRate(DashboardStatisticsType type) {
        saveDashboardStockStatistics(type, userHoldingStockRepository::findAllUserHoldingStockQuantity);
    }

    public void saveDailyPostCount(DashboardStatisticsType type) {
        saveDashboardStockStatisticsBiFunc(type, postRepository::findPostCountByStock);
    }

    public void saveDailyCommentCount(DashboardStatisticsType type) {
        saveDashboardStockStatisticsBiFunc(type, postRepository::findCommentCountByStock);
    }

    public void saveDailyLikedCount(DashboardStatisticsType type) {
        saveDashboardStockStatisticsBiFunc(type, postRepository::findLikedCountByStock);
    }

    private void saveDashboardStockStatisticsBiFunc(
        DashboardStatisticsType type, BiFunction<LocalDateTime, LocalDateTime, List<? extends ICountItem>> function
    ) {
        String date = getStatisticsDate();
        List<DashboardStockStatistics> list = function.apply(getYesterdayStartDateTime(), getYesterdayEndDateTime())
            .stream()
            .map(stockCountDto -> getOrCreateDashboardStockStatistics(stockCountDto.getValue(), stockCountDto.getTitle(), type, date))
            .toList();

        dashboardStockStatisticsRepository.saveAllAndFlush(list);
        setLogMessage(type, list.size(), date);
    }

    private void saveDashboardStockStatistics(
        DashboardStatisticsType type, Function<LocalDateTime, List<? extends ICountItem>> function
    ) {
        String date = getStatisticsDate();
        List<DashboardStockStatistics> list = function.apply(getYesterdayEndDateTime())
            .stream()
            .map(stockCountDto -> getOrCreateDashboardStockStatistics(stockCountDto.getValue(), stockCountDto.getTitle(), type, date))
            .toList();

        dashboardStockStatisticsRepository.saveAllAndFlush(list);
        setLogMessage(type, list.size(), date);
    }

    private void setLogMessage(DashboardStatisticsType type, Object data, String date) {
        log.info("[DashboardStockStatistics] %s batch successfully finished. [data: %s] on %s".formatted(
            type.getDisplayName(), data, date
        ));
    }

    private DashboardStockStatistics getOrCreateDashboardStockStatistics(
        Double count, String stockCode, DashboardStatisticsType type, String date
    ) {
        DashboardStockStatistics dashboardStockStatistics = getDashboardStockStatisticsOrEmpty(stockCode, type, date);

        dashboardStockStatistics.setStockCode(stockCode);
        dashboardStockStatistics.setType(type);
        dashboardStockStatistics.setDate(date);
        dashboardStockStatistics.setValue(count);

        return dashboardStockStatistics;
    }

    private String getStatisticsDate() {
        return KoreanDateTimeUtil.getFormattedYesterdayTime(
            DashboardStatisticsPeriodType.DAILY.getPeriodFormat()
        );
    }

    public List<ag.act.model.DashboardStatisticsResponse> getStatistics(DashboardStatisticsParamDto paramDto) {
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

    private List<DashboardStatisticsCountDto> getDailyDashboardStatisticsCountDtoList(DashboardStatisticsParamDto paramDto) {
        return getAllDailyByTypeInAndDateBetweenOrderByDate(paramDto.getDashboardSearchParamDtoForDailyStockStatistics());
    }

    private List<DashboardStatisticsCountDto> getMonthlyDashboardStatisticsCountDtoList(DashboardStatisticsParamDto paramDto) {
        return getAllMonthlyByTypeInAndDateBetweenOrderByDate(paramDto.getDashboardSearchParamDtoForMonthlyStockStatistics());
    }

    private List<DashboardStatisticsCountDto> getAllDailyByTypeInAndDateBetweenOrderByDate(
        DashboardSearchParamDto dailyStoredSearchParamDto
    ) {
        if (dailyStoredSearchParamDto.getStockCode() != null) {
            return dashboardStockStatisticsRepository.findAllDailyStockByTypeInAndDateBetweenOrderByDate(
                dailyStoredSearchParamDto.getDashboardStatisticsTypeList(),
                dailyStoredSearchParamDto.getStartPeriod(),
                dailyStoredSearchParamDto.getEndPeriod(),
                dailyStoredSearchParamDto.getStockCode()
            );
        }
        return dashboardStockStatisticsRepository.findAllDailyByTypeInAndDateBetweenOrderByDate(
            dailyStoredSearchParamDto.getDashboardStatisticsTypeList(),
            dailyStoredSearchParamDto.getStartPeriod(),
            dailyStoredSearchParamDto.getEndPeriod()
        );
    }

    private List<DashboardStatisticsCountDto> getAllMonthlyByTypeInAndDateBetweenOrderByDate(
        DashboardSearchParamDto dashboardSearchParamDto
    ) {
        if (dashboardSearchParamDto.getStockCode() != null) {
            return dashboardStockStatisticsRepository.findAllMonthlyStockByTypeInAndDateBetweenOrderByDate(
                dashboardSearchParamDto.getDashboardStatisticsTypeList(),
                dashboardSearchParamDto.getStartPeriod(),
                dashboardSearchParamDto.getEndPeriod(),
                dashboardSearchParamDto.getStockCode()
            );
        }
        return dashboardStockStatisticsRepository.findAllMonthlyByTypeInAndDateBetweenOrderByDate(
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

    public void upsertStockUserWithdrawalCount(
        String stockCode, DashboardStatisticsType type
    ) {
        String date = getStatisticsDate();
        DashboardStockStatistics dashboardStockStatistics = getDashboardStockStatisticsOrDefault(stockCode, type, date);

        dashboardStockStatistics.setStockCode(stockCode);
        dashboardStockStatistics.setType(type);
        dashboardStockStatistics.setDate(date);
        dashboardStockStatistics.setValue(dashboardStockStatistics.getValue() + ADD_COUNT);

        dashboardStockStatisticsRepository.saveAndFlush(dashboardStockStatistics);
    }

    @NotNull
    private DashboardStockStatistics getDashboardStockStatisticsOrDefault(String stockCode, DashboardStatisticsType type, String date) {
        return dashboardStockStatisticsRepository.findByTypeAndDateAndStockCode(type, date, stockCode)
            .orElse(new DashboardStockStatistics(type, stockCode, date, DEFAULT_COUNT));
    }

    @NotNull
    private DashboardStockStatistics getDashboardStockStatisticsOrEmpty(String stockCode, DashboardStatisticsType type, String date) {
        return dashboardStockStatisticsRepository.findByTypeAndDateAndStockCode(type, date, stockCode)
            .orElseGet(DashboardStockStatistics::new);
    }

    private LocalDateTime getYesterdayEndDateTime() {
        return KoreanDateTimeUtil.getYesterdayEndDateTime();
    }

    private LocalDateTime getYesterdayStartDateTime() {
        return KoreanDateTimeUtil.getYesterdayStartDateTime();
    }
}
