package ag.act.enums.admin;

import ag.act.dto.admin.DashboardStatisticsGenderCountDto;
import lombok.Getter;

import java.util.function.ToLongFunction;

@Getter
public enum GenderTitle {
    MALE("남") {
        @Override
        public ToLongFunction<? super DashboardStatisticsGenderCountDto> getMapper() {
            return DashboardStatisticsGenderCountDto::getMaleValue;
        }
    },
    FEMALE("여") {
        @Override
        public ToLongFunction<? super DashboardStatisticsGenderCountDto> getMapper() {
            return DashboardStatisticsGenderCountDto::getFemaleValue;
        }
    };

    private final String displayName;

    GenderTitle(String displayName) {
        this.displayName = displayName;
    }

    public abstract ToLongFunction<? super DashboardStatisticsGenderCountDto> getMapper();
}
