package ag.act.service.admin.dashboard;


import ag.act.converter.dashboard.DashboardAgeStatisticsResponseConverter;
import ag.act.dto.admin.DashboardStatisticsAgeCountDto;
import ag.act.dto.admin.DashboardStatisticsParamDto;
import ag.act.entity.admin.DashboardAgeStatistics;
import ag.act.enums.admin.DashboardStatisticsPeriodType;
import ag.act.enums.admin.DashboardStatisticsType;
import ag.act.model.Status;
import ag.act.module.dashboard.statistics.ICountItem;
import ag.act.repository.UserRepository;
import ag.act.repository.admin.DashboardAgeStatisticsRepository;
import ag.act.util.KoreanDateTimeUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DashboardAgeStatisticsService {
    private final UserRepository userRepository;
    private final DashboardAgeStatisticsRepository dashboardAgeStatisticsRepository;
    private final DashboardAgeStatisticsResponseConverter dashboardAgeStatisticsResponseConverter;

    public void saveDailyUserAgeCount(DashboardStatisticsType type) {
        List<ICountItem> ageGroups = getAgeGroups();
        String date = getDate(DashboardStatisticsPeriodType.DAILY.getPeriodFormat());
        DashboardAgeStatistics dashboardAgeStatistics = getDashboardAgeStatistics(type, date);

        dashboardAgeStatistics.setType(type);
        dashboardAgeStatistics.setDate(date);
        dashboardAgeStatistics.setAge10Value(getAge(ageGroups, "10s"));
        dashboardAgeStatistics.setAge20Value(getAge(ageGroups, "20s"));
        dashboardAgeStatistics.setAge30Value(getAge(ageGroups, "30s"));
        dashboardAgeStatistics.setAge40Value(getAge(ageGroups, "40s"));
        dashboardAgeStatistics.setAge50Value(getAge(ageGroups, "50s"));
        dashboardAgeStatistics.setAge60Value(getAge(ageGroups, "60s"));
        dashboardAgeStatistics.setAge70Value(getAge(ageGroups, "70s"));
        dashboardAgeStatistics.setAge80Value(getAge(ageGroups, "80s"));
        dashboardAgeStatistics.setAge90Value(getAge(ageGroups, "90s"));

        dashboardAgeStatisticsRepository.save(dashboardAgeStatistics);
        setLogMessage(type, ageGroups.size(), date);
    }

    private DashboardAgeStatistics getDashboardAgeStatistics(DashboardStatisticsType type, String date) {
        return dashboardAgeStatisticsRepository.findByTypeAndDate(type, date)
            .orElseGet(DashboardAgeStatistics::new);
    }

    private Long getAge(List<ICountItem> ageGroups, String targetGroup) {
        return ageGroups.stream().filter(it -> targetGroup.equals(it.getTitle())).findFirst().map(ICountItem::getLongValue).orElse(0L);
    }

    private void setLogMessage(DashboardStatisticsType type, Object data, String date) {
        log.info("[DashboardAgeStatistics] %s batch successfully finished. [data: %s] on %s".formatted(
            type.getDisplayName(), data, date
        ));
    }

    private List<ICountItem> getAgeGroups() {
        return userRepository.findAgeGroupByStatusIn(
            List.of(Status.ACTIVE.name())
        );
    }

    private String getDate(String format) {
        return KoreanDateTimeUtil.getFormattedYesterdayTime(format);
    }

    public ag.act.model.DashboardAgeStatisticsResponse getStatistics(DashboardStatisticsParamDto paramDto) {
        return dashboardAgeStatisticsResponseConverter.convert(paramDto, DashboardStatisticsType.DAILY_USER_AGE_COUNT, getData(paramDto));
    }

    private List<DashboardStatisticsAgeCountDto> getData(DashboardStatisticsParamDto paramDto) {
        if (DashboardStatisticsPeriodType.MONTHLY == paramDto.getPeriodType()) {
            return getMonthlyByTypeAndDateBetweenOrderByDate(paramDto);
        }
        return getDailyByTypeAndDateBetweenOrderByDate(paramDto);
    }

    private List<DashboardStatisticsAgeCountDto> getDailyByTypeAndDateBetweenOrderByDate(
        DashboardStatisticsParamDto paramDto
    ) {
        return dashboardAgeStatisticsRepository.findByTypeAndInDate(
            DashboardStatisticsType.DAILY_USER_AGE_COUNT.getDateList(
                paramDto.getPeriodType(), paramDto.getSearchFrom(), paramDto.getSearchTo()
            )
        );
    }

    private List<DashboardStatisticsAgeCountDto> getMonthlyByTypeAndDateBetweenOrderByDate(
        DashboardStatisticsParamDto paramDto
    ) {
        return dashboardAgeStatisticsRepository.findByTypeAndInDate(
            DashboardStatisticsType.DAILY_USER_AGE_COUNT.getDateList(
                paramDto.getPeriodType(), paramDto.getSearchFrom(), paramDto.getSearchTo()
            )
        );
    }
}
