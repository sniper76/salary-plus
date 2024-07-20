package ag.act.enums.admin;

import ag.act.dto.admin.DashboardStatisticsAgeCountDto;
import lombok.Getter;

import java.util.function.ToLongFunction;

@Getter
public enum AgeTitle {
    AGE10("10대 이하") {
        @Override
        public ToLongFunction<? super DashboardStatisticsAgeCountDto> getMapper() {
            return DashboardStatisticsAgeCountDto::getAge10Value;
        }
    },
    AGE20("20대") {
        @Override
        public ToLongFunction<? super DashboardStatisticsAgeCountDto> getMapper() {
            return DashboardStatisticsAgeCountDto::getAge20Value;
        }
    },
    AGE30("30대") {
        @Override
        public ToLongFunction<? super DashboardStatisticsAgeCountDto> getMapper() {
            return DashboardStatisticsAgeCountDto::getAge30Value;
        }
    },
    AGE40("40대") {
        @Override
        public ToLongFunction<? super DashboardStatisticsAgeCountDto> getMapper() {
            return DashboardStatisticsAgeCountDto::getAge40Value;
        }
    },
    AGE50("50대") {
        @Override
        public ToLongFunction<? super DashboardStatisticsAgeCountDto> getMapper() {
            return DashboardStatisticsAgeCountDto::getAge50Value;
        }
    },
    AGE60("60대") {
        @Override
        public ToLongFunction<? super DashboardStatisticsAgeCountDto> getMapper() {
            return DashboardStatisticsAgeCountDto::getAge60Value;
        }
    },
    AGE70OVER("70대 이상") {
        @Override
        public ToLongFunction<? super DashboardStatisticsAgeCountDto> getMapper() {
            return DashboardStatisticsAgeCountDto::getAge70AndOver;
        }
    },
    ;

    private final String displayName;

    AgeTitle(String displayName) {
        this.displayName = displayName;
    }

    public abstract ToLongFunction<? super DashboardStatisticsAgeCountDto> getMapper();
}
