package ag.act.dto.admin;

import ag.act.enums.admin.DashboardStatisticsPeriodType;
import ag.act.enums.admin.DashboardStatisticsType;
import ag.act.util.KoreanDateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static ag.act.enums.admin.DashboardStatisticsPeriodType.DAILY;
import static ag.act.enums.admin.DashboardStatisticsPeriodType.MONTHLY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsParamDto {
    private static final int DEFAULT_PERIOD = 7;
    private static final DateTimeFormatter FORMATTER_YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FORMATTER_YYYY_MM = DateTimeFormatter.ofPattern("yyyy-MM");
    private List<DashboardStatisticsType> typeList;
    private String stockCode;
    private DashboardStatisticsPeriodType periodType;
    private int period;
    private String searchFrom;
    private String searchTo;

    public DashboardStatisticsParamDto(
        String type, String stockCode, String periodType, int period, String searchFrom, String searchTo
    ) {
        this.typeList = parseDashboardStatisticsTypes(type);
        this.stockCode = stockCode;
        this.periodType = DashboardStatisticsPeriodType.fromValue(periodType);
        this.period = period;
        this.searchFrom = fillSearchFromDate(searchFrom);
        this.searchTo = fillSearchToDate(searchTo);
    }

    public DashboardStatisticsParamDto(String periodType, int period, String searchFrom, String searchTo) {
        this.periodType = DashboardStatisticsPeriodType.fromValue(periodType);
        this.typeList = List.of();
        this.period = period;
        this.searchFrom = fillAgeAndGenderSearchFromDate(searchFrom);
        this.searchTo = fillSearchToDate(searchTo);
    }

    private String fillSearchToDate(String searchDate) {
        if (!StringUtils.isBlank(searchDate)) {
            return searchDate;
        }
        if (periodType == MONTHLY) {
            return KoreanDateTimeUtil.getYesterdayZonedDateTime().format(FORMATTER_YYYY_MM);
        }
        return KoreanDateTimeUtil.getYesterdayZonedDateTime().format(FORMATTER_YYYY_MM_DD);
    }

    private String fillAgeAndGenderSearchFromDate(String searchDate) {
        if (!StringUtils.isBlank(searchDate)) {
            return searchDate;
        }
        if (periodType == MONTHLY) {
            return KoreanDateTimeUtil.getYesterdayZonedDateTime().minusMonths(1).format(FORMATTER_YYYY_MM);
        }
        return KoreanDateTimeUtil.getYesterdayZonedDateTime().minusDays(1).format(FORMATTER_YYYY_MM_DD);
    }

    private String fillSearchFromDate(String searchDate) {
        if (!StringUtils.isBlank(searchDate)) {
            return searchDate;
        }
        if (periodType == MONTHLY) {
            return KoreanDateTimeUtil.getYesterdayZonedDateTime().minusMonths(6).format(FORMATTER_YYYY_MM);
        }
        return KoreanDateTimeUtil.getYesterdayZonedDateTime().minusDays(6).format(FORMATTER_YYYY_MM_DD);
    }

    private List<DashboardStatisticsType> parseDashboardStatisticsTypes(String type) {
        try {
            return List.of(DashboardStatisticsType.fromValue(type));
        } catch (Exception e) {
            return getDashboardStatisticsTypeList();
        }
    }

    private List<DashboardStatisticsType> getDashboardStatisticsTypeList() {
        return Arrays.stream(DashboardStatisticsType.values()).toList();
    }

    public DashboardSearchParamDto getDashboardSearchParamDtoForDailyExceptTotalAssetPrice() {
        List<String> searchDateList = DashboardStatisticsType.DAILY_USER_REGISTRATION_COUNT.getDateList(
            periodType, searchFrom, searchTo
        );
        return new DashboardSearchParamDto(
            getDailyTotalAssetPriceExceptSearchDashboardStatisticsTypeList(),
            searchDateList.get(0),
            searchDateList.get(searchDateList.size() - 1)
        );
    }

    public DashboardSearchParamDto getDashboardSearchParamDtoForDailyTotalAssetPrice() {
        return new DashboardSearchParamDto(
            getDailyTotalAssetPriceSearchDashboardStatisticsTypeList(),
            DashboardStatisticsType.DAILY_TOTAL_ASSET_PRICE.getDateList(
                periodType, searchFrom, searchTo
            )
        );
    }

    public DashboardSearchParamDto getDashboardSearchParamDtoForMonthlySumStatistics() {
        List<String> searchDateList = DashboardStatisticsType.DAILY_USER_REGISTRATION_COUNT.getDateList(
            periodType, searchFrom, searchTo
        );
        return new DashboardSearchParamDto(
            getDailyTotalAssetPriceAndDauExceptSearchDashboardStatisticsTypeList(),
            searchDateList.get(0),
            searchDateList.get(searchDateList.size() - 1)
        );
    }

    public DashboardSearchParamDto getDashboardSearchParamDtoForMonthlyTotalAssetPrice() {
        return new DashboardSearchParamDto(
            getDailyTotalAssetPriceSearchDashboardStatisticsTypeList(),
            DashboardStatisticsType.DAILY_TOTAL_ASSET_PRICE.getDateList(
                periodType, searchFrom, searchTo
            )
        );
    }

    public DashboardSearchParamDto getDashboardSearchParamDtoForMonthlyActiveUser() {
        return new DashboardSearchParamDto(
            getMonthlyActiveUserSearchDashboardStatisticsTypeList(),
            DashboardStatisticsType.MONTHLY_ACTIVE_USER.getDateList(
                periodType, searchFrom, searchTo
            )
        );
    }

    public DashboardSearchParamDto getDashboardSearchParamDtoForDailyStockStatistics() {
        List<String> searchDateList = DashboardStatisticsType.DAILY_STOCK_LIKED_COUNT.getDateList(
            periodType, searchFrom, searchTo
        );
        return new DashboardSearchParamDto(
            getDailyTotalAssetPriceExceptSearchDashboardStatisticsTypeList(),
            searchDateList.get(0),
            searchDateList.get(searchDateList.size() - 1),
            stockCode
        );
    }

    public DashboardSearchParamDto getDashboardSearchParamDtoForMonthlyStockStatistics() {
        List<String> searchDateList = DashboardStatisticsType.DAILY_STOCK_LIKED_COUNT.getDateList(
            periodType, searchFrom, searchTo
        );
        return new DashboardSearchParamDto(
            getDailyTotalAssetPriceExceptSearchDashboardStatisticsTypeList(),
            searchDateList.get(0),
            searchDateList.get(searchDateList.size() - 1),
            stockCode
        );
    }

    private List<DashboardStatisticsType> getDailyTotalAssetPriceExceptSearchDashboardStatisticsTypeList() {
        return filterDashboardStatisticsTypes(DAILY).stream().filter(this::isNotAccumulateType).toList();
    }

    private List<DashboardStatisticsType> getDailyTotalAssetPriceAndDauExceptSearchDashboardStatisticsTypeList() {
        return filterDashboardStatisticsTypes(DAILY).stream()
            .filter(this::isNotAccumulateTypeOrActiveUser)
            .toList();
    }

    private List<DashboardStatisticsType> getDailyTotalAssetPriceSearchDashboardStatisticsTypeList() {
        return filterDashboardStatisticsTypes(DAILY).stream().filter(this::isAccumulateType).toList();
    }

    private boolean isNotAccumulateTypeOrActiveUser(DashboardStatisticsType type) {
        return !List.of(
            DashboardStatisticsType.DAILY_TOTAL_ASSET_PRICE,
            DashboardStatisticsType.DAILY_USER_ACTIVE_MY_DATA_ACCESS_ACCUMULATE,
            DashboardStatisticsType.DAILY_ACTIVE_USER
        ).contains(type);
    }

    private boolean isNotAccumulateType(DashboardStatisticsType type) {
        return !isAccumulateType(type);
    }

    private boolean isAccumulateType(DashboardStatisticsType type) {
        return type == DashboardStatisticsType.DAILY_TOTAL_ASSET_PRICE
            || type == DashboardStatisticsType.DAILY_USER_ACTIVE_MY_DATA_ACCESS_ACCUMULATE;
    }

    private List<DashboardStatisticsType> getMonthlyActiveUserSearchDashboardStatisticsTypeList() {
        return filterDashboardStatisticsTypes(MONTHLY);
    }

    private List<DashboardStatisticsType> filterDashboardStatisticsTypes(DashboardStatisticsPeriodType type) {
        return typeList.stream().filter(it -> it.getPeriodType() == type).toList();
    }
}
