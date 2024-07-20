package ag.act.service.admin.dashboard;


import ag.act.converter.dashboard.DashboardGenderStatisticsResponseConverter;
import ag.act.dto.admin.DashboardStatisticsGenderCountDto;
import ag.act.dto.admin.DashboardStatisticsParamDto;
import ag.act.entity.admin.DashboardGenderStatistics;
import ag.act.enums.admin.DashboardStatisticsPeriodType;
import ag.act.enums.admin.DashboardStatisticsType;
import ag.act.model.DashboardGenderStatisticsResponse;
import ag.act.model.Gender;
import ag.act.model.Status;
import ag.act.module.dashboard.statistics.ICountItem;
import ag.act.repository.UserRepository;
import ag.act.repository.admin.DashboardGenderStatisticsRepository;
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
public class DashboardGenderStatisticsService {
    private final UserRepository userRepository;
    private final DashboardGenderStatisticsRepository dashboardGenderStatisticsRepository;
    private final DashboardGenderStatisticsResponseConverter dashboardGenderStatisticsResponseConverter;

    public void saveDailyUserGenderCount(DashboardStatisticsType type) {
        List<ICountItem> itemList = getGenderCounts();
        String date = getDate(DashboardStatisticsPeriodType.DAILY.getPeriodFormat());
        DashboardGenderStatistics dashboardGenderStatistics = getDashboardGenderStatistics(type, date);

        dashboardGenderStatistics.setType(type);
        dashboardGenderStatistics.setDate(date);
        dashboardGenderStatistics.setMaleValue(getGenderCount(itemList, Gender.M));
        dashboardGenderStatistics.setFemaleValue(getGenderCount(itemList, Gender.F));

        dashboardGenderStatisticsRepository.save(dashboardGenderStatistics);
        setLogMessage(type, itemList.size(), date);
    }

    private List<ICountItem> getGenderCounts() {
        return userRepository.findGenderCountByStatusIn(
            List.of(Status.ACTIVE.name())
        );
    }

    private DashboardGenderStatistics getDashboardGenderStatistics(DashboardStatisticsType type, String date) {
        return dashboardGenderStatisticsRepository.findByTypeAndDate(type, date)
            .orElseGet(DashboardGenderStatistics::new);
    }

    private Long getGenderCount(List<ICountItem> itemList, Gender gender) {
        return itemList.stream().filter(it -> gender.name().equals(it.getTitle())).findFirst().map(ICountItem::getLongValue).orElse(0L);
    }

    private void setLogMessage(DashboardStatisticsType type, Object data, String date) {
        log.info("[DashboardGenderStatistics] %s batch successfully finished. [data: %s] on %s".formatted(
            type.getDisplayName(), data, date
        ));
    }

    private String getDate(String format) {
        return KoreanDateTimeUtil.getFormattedYesterdayTime(format);
    }

    public DashboardGenderStatisticsResponse getStatistics(DashboardStatisticsParamDto paramDto) {
        return dashboardGenderStatisticsResponseConverter.convert(paramDto, DashboardStatisticsType.DAILY_USER_GENDER_COUNT, getData(paramDto));
    }

    private List<DashboardStatisticsGenderCountDto> getData(DashboardStatisticsParamDto paramDto) {
        if (DashboardStatisticsPeriodType.MONTHLY == paramDto.getPeriodType()) {
            return getMonthlyTypeAndDateBetweenOrderByDate(paramDto);
        }
        return getDailyTypeAndDateBetweenOrderByDate(paramDto);
    }

    private List<DashboardStatisticsGenderCountDto> getDailyTypeAndDateBetweenOrderByDate(
        DashboardStatisticsParamDto paramDto
    ) {
        return dashboardGenderStatisticsRepository.findByTypeAndInDate(
            DashboardStatisticsType.DAILY_USER_GENDER_COUNT.getDateList(
                paramDto.getPeriodType(), paramDto.getSearchFrom(), paramDto.getSearchTo()
            )
        );
    }

    private List<DashboardStatisticsGenderCountDto> getMonthlyTypeAndDateBetweenOrderByDate(
        DashboardStatisticsParamDto paramDto
    ) {
        return dashboardGenderStatisticsRepository.findByTypeAndInDate(
            DashboardStatisticsType.DAILY_USER_GENDER_COUNT.getDateList(
                paramDto.getPeriodType(), paramDto.getSearchFrom(), paramDto.getSearchTo()
            )
        );
    }
}
