package ag.act.converter.dashboard;

import ag.act.dto.admin.DashboardStatisticsCountDto;
import ag.act.dto.admin.DashboardStatisticsParamDto;
import ag.act.enums.admin.DashboardStatisticsPeriodType;
import ag.act.enums.admin.DashboardStatisticsType;
import ag.act.model.DashboardStatisticsResponse;
import ag.act.model.DashboardStatisticsResponseItemsInner;
import ag.act.model.DashboardStatisticsResponseSummary;
import ag.act.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static ag.act.enums.admin.DashboardStatisticsPeriodType.DAILY;
import static ag.act.enums.admin.DashboardStatisticsPeriodType.MONTHLY;

@Component
@RequiredArgsConstructor
public class DashboardStatisticsResponseConverter {
    private final DashboardStatisticsResponseItemsInnerConverter dashboardStatisticsResponseItemsInnerConverter;
    private final DashboardStatisticsResponseSummaryConverter dashboardStatisticsResponseSummaryConverter;

    public DashboardStatisticsResponse convert(
        DashboardStatisticsParamDto paramDto,
        DashboardStatisticsType type,
        List<DashboardStatisticsCountDto> statisticsList
    ) {
        DashboardStatisticsPeriodType periodType = getPeriodType(paramDto, type);
        final List<DashboardStatisticsCountDto> fullyPopulatedStatisticsList = getFullyPopulatedStatisticsListWithMissingDates(
            paramDto,
            type,
            statisticsList
        );
        return new DashboardStatisticsResponse()
            .summary(getSummary(periodType, fullyPopulatedStatisticsList))
            .items(getDataItems(fullyPopulatedStatisticsList))
            .value(getDataValue(fullyPopulatedStatisticsList))
            .title(type.getDisplayName())
            .type(type.name())
            .periodType(periodType.name());
    }

    private List<DashboardStatisticsCountDto> getFullyPopulatedStatisticsListWithMissingDates(
        DashboardStatisticsParamDto paramDto,
        DashboardStatisticsType type,
        List<DashboardStatisticsCountDto> statisticsList
    ) {
        // 여기서 비여있는 데이터를 채운다
        List<String> missingSearchDateList = getMissingSearchDateList(statisticsList, getSearchDateList(paramDto, type));

        return reverseSortByDate(
            Stream.concat(
                statisticsList.stream(),
                getFilledInStatisticsList(type, missingSearchDateList).stream()
            ).toList()
        );
    }

    private List<DashboardStatisticsCountDto> getFilledInStatisticsList(
        DashboardStatisticsType type, List<String> missingSearchDateList
    ) {
        return missingSearchDateList.stream()
            .map(date -> new DashboardStatisticsCountDto(type, date, 0d))
            .toList();
    }

    private List<DashboardStatisticsCountDto> reverseSortByDate(List<DashboardStatisticsCountDto> filledInStatisticsList) {
        return filledInStatisticsList.stream().sorted(Comparator.comparing(DashboardStatisticsCountDto::getDate).reversed()).toList();
    }

    private List<String> getMissingSearchDateList(List<DashboardStatisticsCountDto> statisticsListForType, List<String> searchDateList) {
        return searchDateList.stream()
            .filter(it -> statisticsListForType.stream().noneMatch(e -> e.getDate().equals(it)))
            .toList();
    }

    private List<String> getSearchDateList(DashboardStatisticsParamDto paramDto, DashboardStatisticsType type) {
        if (DashboardStatisticsType.isMonthlyType(type) && paramDto.getPeriodType() == MONTHLY) {
            return type.getDateList(paramDto.getPeriodType(), paramDto.getSearchFrom(), paramDto.getSearchTo())
                .stream()
                .map(DateTimeUtil::extractYearMonth)
                .toList();
        }
        return type.getDateList(paramDto.getPeriodType(), paramDto.getSearchFrom(), paramDto.getSearchTo());
    }

    private DashboardStatisticsPeriodType getPeriodType(DashboardStatisticsParamDto paramDto, DashboardStatisticsType type) {
        return List.of(paramDto.getPeriodType(), type.getPeriodType()).contains(MONTHLY) ? MONTHLY : DAILY;
    }

    private BigDecimal getDataValue(List<DashboardStatisticsCountDto> statisticsListForType) {
        return statisticsListForType.stream()
            .findFirst()
            .map(DashboardStatisticsCountDto::getBigDecimalValue)
            .orElse(null);
    }

    private List<DashboardStatisticsResponseItemsInner> getDataItems(
        List<DashboardStatisticsCountDto> statisticsListForType
    ) {
        return statisticsListForType.stream().map(dashboardStatisticsResponseItemsInnerConverter::convert).toList();
    }

    private DashboardStatisticsResponseSummary getSummary(
        DashboardStatisticsPeriodType periodType, List<DashboardStatisticsCountDto> statisticsList
    ) {
        DashboardStatisticsCountDto currentCountDto = statisticsList.get(0);
        DashboardStatisticsCountDto pastCountDto = statisticsList.get(1);
        return dashboardStatisticsResponseSummaryConverter.convert(periodType, currentCountDto.getValue(), pastCountDto.getValue());
    }
}
