package ag.act.converter.dashboard;

import ag.act.dto.admin.DashboardStatisticsAgeCountDto;
import ag.act.dto.admin.DashboardStatisticsParamDto;
import ag.act.enums.admin.AgeTitle;
import ag.act.enums.admin.DashboardStatisticsPeriodType;
import ag.act.enums.admin.DashboardStatisticsType;
import ag.act.model.DashboardAgeStatisticsResponse;
import ag.act.model.DashboardStatisticsItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class DashboardAgeStatisticsResponseConverter {
    private final DashboardStatisticsItemResponseConverter dashboardStatisticsItemResponseConverter;

    public DashboardAgeStatisticsResponse convert(
        DashboardStatisticsParamDto paramDto,
        DashboardStatisticsType type,
        List<DashboardStatisticsAgeCountDto> listDto
    ) {
        DashboardStatisticsPeriodType periodType = paramDto.getPeriodType();
        if (listDto.isEmpty()) {
            return new DashboardAgeStatisticsResponse()
                .total(0L)
                .title(type.getDisplayName())
                .type(type.name())
                .periodType(periodType.name());
        }

        DashboardStatisticsAgeComparison comparison = getComparison(listDto);

        return new DashboardAgeStatisticsResponse()
            .age10(getDataItem(AgeTitle.AGE10, comparison, periodType))
            .age20(getDataItem(AgeTitle.AGE20, comparison, periodType))
            .age30(getDataItem(AgeTitle.AGE30, comparison, periodType))
            .age40(getDataItem(AgeTitle.AGE40, comparison, periodType))
            .age50(getDataItem(AgeTitle.AGE50, comparison, periodType))
            .age60(getDataItem(AgeTitle.AGE60, comparison, periodType))
            .age70(getDataItem(AgeTitle.AGE70OVER, comparison, periodType))
            .total(comparison.getCurrentTotal())
            .title(type.getDisplayName())
            .type(type.name())
            .periodType(periodType.name());
    }

    private DashboardStatisticsAgeComparison getComparison(List<DashboardStatisticsAgeCountDto> listDto) {
        if (listDto.size() == 1) {
            return new DashboardStatisticsAgeComparison(listDto.get(0), DashboardStatisticsAgeCountDto.empty());
        }
        return new DashboardStatisticsAgeComparison(listDto.get(0), listDto.get(1));
    }

    private DashboardStatisticsItemResponse getDataItem(
        AgeTitle ageTitle,
        DashboardStatisticsAgeComparison comparison,
        DashboardStatisticsPeriodType periodType
    ) {
        final ToLongFunction<? super DashboardStatisticsAgeCountDto> mapper = ageTitle.getMapper();

        return dashboardStatisticsItemResponseConverter.convert(
            comparison.getCurrentValue(mapper),
            comparison.getPastValue(mapper),
            comparison.getCurrentTotal(),
            ageTitle.getDisplayName(),
            periodType
        );
    }

    private record DashboardStatisticsAgeComparison(
        DashboardStatisticsAgeCountDto currentCountDto,
        DashboardStatisticsAgeCountDto pastCountDto
    ) {
        public long getCurrentValue(ToLongFunction<? super DashboardStatisticsAgeCountDto> mapper) {
            return Stream.of(currentCountDto).mapToLong(mapper).sum();
        }

        public long getPastValue(ToLongFunction<? super DashboardStatisticsAgeCountDto> mapper) {
            return Stream.of(pastCountDto).mapToLong(mapper).sum();
        }

        public long getCurrentTotal() {
            return currentCountDto.getTotal();
        }
    }
}