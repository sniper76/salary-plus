package ag.act.converter.dashboard;

import ag.act.dto.admin.DashboardStatisticsGenderCountDto;
import ag.act.dto.admin.DashboardStatisticsParamDto;
import ag.act.enums.admin.DashboardStatisticsPeriodType;
import ag.act.enums.admin.DashboardStatisticsType;
import ag.act.enums.admin.GenderTitle;
import ag.act.model.DashboardGenderStatisticsResponse;
import ag.act.model.DashboardStatisticsItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class DashboardGenderStatisticsResponseConverter {
    private final DashboardStatisticsItemResponseConverter dashboardStatisticsItemResponseConverter;

    public DashboardGenderStatisticsResponse convert(
        DashboardStatisticsParamDto paramDto,
        DashboardStatisticsType type,
        List<DashboardStatisticsGenderCountDto> listDto
    ) {
        DashboardStatisticsPeriodType periodType = paramDto.getPeriodType();
        if (listDto.isEmpty()) {
            return new DashboardGenderStatisticsResponse()
                .total(0L)
                .title(type.getDisplayName())
                .type(type.name())
                .periodType(periodType.name());
        }

        DashboardStatisticsGenderComparison comparison = getComparison(listDto);

        return new DashboardGenderStatisticsResponse()
            .male(getDataItem(GenderTitle.MALE, comparison, periodType))
            .female(getDataItem(GenderTitle.FEMALE, comparison, periodType))
            .total(comparison.getCurrentTotal())
            .title(type.getDisplayName())
            .type(type.name())
            .periodType(periodType.name());
    }

    private DashboardStatisticsGenderComparison getComparison(List<DashboardStatisticsGenderCountDto> listDto) {
        if (listDto.size() == 1) {
            return new DashboardStatisticsGenderComparison(listDto.get(0), DashboardStatisticsGenderCountDto.empty());
        }
        return new DashboardStatisticsGenderComparison(listDto.get(0), listDto.get(1));
    }

    private DashboardStatisticsItemResponse getDataItem(
        GenderTitle genderTitle,
        DashboardStatisticsGenderComparison comparison,
        DashboardStatisticsPeriodType periodType
    ) {
        final ToLongFunction<? super DashboardStatisticsGenderCountDto> mapper = genderTitle.getMapper();

        return dashboardStatisticsItemResponseConverter.convert(
            comparison.getCurrentValue(mapper),
            comparison.getPastValue(mapper),
            comparison.getCurrentTotal(),
            genderTitle.getDisplayName(),
            periodType
        );
    }

    private record DashboardStatisticsGenderComparison(
        DashboardStatisticsGenderCountDto currentCountDto,
        DashboardStatisticsGenderCountDto pastCountDto
    ) {
        public long getCurrentValue(ToLongFunction<? super DashboardStatisticsGenderCountDto> mapper) {
            return Stream.of(currentCountDto).mapToLong(mapper).sum();
        }

        public long getPastValue(ToLongFunction<? super DashboardStatisticsGenderCountDto> mapper) {
            return Stream.of(pastCountDto).mapToLong(mapper).sum();
        }

        public long getCurrentTotal() {
            return currentCountDto.getTotal();
        }
    }
}