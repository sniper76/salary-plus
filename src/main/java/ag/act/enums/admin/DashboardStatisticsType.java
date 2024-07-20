package ag.act.enums.admin;

import ag.act.util.KoreanDateTimeUtil;
import ag.act.util.ZoneIdUtil;
import lombok.Getter;

import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static ag.act.enums.admin.DashboardStatisticsPeriodType.DAILY;
import static ag.act.enums.admin.DashboardStatisticsPeriodType.MONTHLY;

@Getter
public enum DashboardStatisticsType {
    DAILY_USER_REGISTRATION_COUNT("가입자수", DAILY, 0) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            return getDateListForBetweenSearch(periodType, from, to);
        }
    },
    DAILY_USER_WITHDRAWAL_COUNT("탈퇴자수", DAILY, 1) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            return getDateListForBetweenSearch(periodType, from, to);
        }
    },
    DAILY_ACTIVE_USER("DAU", DAILY, 2) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            return getDateListForBetweenSearch(periodType, from, to);
        }
    },
    MONTHLY_ACTIVE_USER("MAU", MONTHLY, 2) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            List<String> dateList = new ArrayList<>();
            ZonedDateTime fromZonedDateTime = formattedKoreaZonedDateTime(MONTHLY, from);
            ZonedDateTime toZonedDateTime = formattedKoreaZonedDateTime(MONTHLY, to);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            do {
                dateList.add(fromZonedDateTime.format(formatter));

                fromZonedDateTime = MONTHLY.plus(fromZonedDateTime);
            }
            while (!fromZonedDateTime.isAfter(toZonedDateTime));
            return dateList;
        }
    },
    DAILY_TOTAL_ASSET_PRICE("총자산", DAILY, 3) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            return getDateListByAccumulateSearch(periodType, from, to);
        }
    },
    DAILY_USER_LOGIN_COUNT("로그인횟수", DAILY, 4) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            return getDateListForBetweenSearch(periodType, from, to);
        }
    },
    DAILY_USER_REUSE_RATE("액트앱재사용율", DAILY, 5) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            return getDateListForBetweenSearch(periodType, from, to);
        }
    },
    DAILY_POST_VIEW_COUNT("총게시글조회수", DAILY, 6) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            return getDateListForBetweenSearch(periodType, from, to);
        }
    },
    DAILY_USER_ACCESS_PIN_NUMBER_COUNT("인당평균로그인횟수", DAILY, 7) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            return getDateListForBetweenSearch(periodType, from, to);
        }
    },
    DAILY_USER_STATUS_ACTIVE_COUNT("활성회원수", DAILY, 8) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            return getDateListForBetweenSearch(periodType, from, to);
        }
    },
    DAILY_USER_STATUS_PROCESSING_COUNT("가입진행중회원수", DAILY, 9) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            return getDateListForBetweenSearch(periodType, from, to);
        }
    },
    DAILY_USER_STATUS_WITHDRAWAL_COUNT("탈퇴요청중회원수", DAILY, 10) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            return getDateListForBetweenSearch(periodType, from, to);
        }
    },
    DAILY_USER_ACTIVE_MY_DATA_ACCESS_COUNT("마이데이터일조회수", DAILY, 11) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            return getDateListForBetweenSearch(periodType, from, to);
        }
    },
    DAILY_USER_ACTIVE_MY_DATA_ACCESS_ACCUMULATE("마이데이터누적연동수", DAILY, 12) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            return getDateListForBetweenSearch(periodType, from, to);
        }
    },
    DAILY_USER_GENDER_COUNT("성별수", DAILY, 21) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            return getDateListForAgeAndGender(periodType, from, to);
        }
    },
    DAILY_USER_AGE_COUNT("연령별수", DAILY, 22) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            return getDateListForAgeAndGender(periodType, from, to);
        }
    },
    DAILY_STOCK_POST_COUNT("게시글업로드수", DAILY, 31) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            return getDateListForBetweenSearch(periodType, from, to);
        }
    },
    DAILY_STOCK_COMMENT_COUNT("댓글수", DAILY, 32) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            return getDateListForBetweenSearch(periodType, from, to);
        }
    },
    DAILY_STOCK_USER_HOLDING_COUNT("지분유입", DAILY, 33) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            return getDateListForBetweenSearch(periodType, from, to);
        }
    },
    DAILY_STOCK_MEMBER_COUNT("유저유입", DAILY, 34) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            return getDateListForBetweenSearch(periodType, from, to);
        }
    },
    DAILY_STOCK_LIKED_COUNT("좋아요수", DAILY, 35) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            return getDateListForBetweenSearch(periodType, from, to);
        }
    },
    DAILY_STOCK_USER_WITHDRAWAL_COUNT("종목별탈퇴자수", DAILY, 1) {
        @Override
        public List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to) {
            return getDateListForBetweenSearch(periodType, from, to);
        }
    };

    private final String displayName;
    private final DashboardStatisticsPeriodType periodType;
    private final int displayOrder;

    DashboardStatisticsType(String displayName, DashboardStatisticsPeriodType periodType, int displayOrder) {
        this.displayName = displayName;
        this.periodType = periodType;
        this.displayOrder = displayOrder;
    }

    private static List<String> getDateListForBetweenSearch(DashboardStatisticsPeriodType periodType, String from, String to) {
        /*
        일별 2023-11-29 2023-11-30 2023-12-01 2023-12-02
        월별 2023-09-01 2023-10-01 2023-11-01 yesterday
        월별조회시 2023-09-01 yesterday 사용
         */
        List<String> dateList = new ArrayList<>();
        ZonedDateTime fromZonedDateTime = getStartDateTime(periodType, from);
        ZonedDateTime toZonedDateTime = getYesterdayOrEndDateOfMonth(periodType, to);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneIdUtil.getSeoulZoneId());

        do {
            dateList.add(fromZonedDateTime.format(formatter));

            fromZonedDateTime = periodType.plus(fromZonedDateTime);
        }
        while (!fromZonedDateTime.isAfter(toZonedDateTime));

        int size = dateList.size();
        dateList.set(size - 1, toZonedDateTime.format(formatter));
        return dateList;
    }

    private static List<String> getDateListByAccumulateSearch(DashboardStatisticsPeriodType periodType, String from, String to) {
        List<String> dateList = new ArrayList<>();
        ZonedDateTime fromZonedDateTime = getEndDateTime(periodType, from);
        ZonedDateTime toZonedDateTime = getYesterdayOrEndDateOfMonth(periodType, to);

        /*
        총자산,마이데이터누적연동수 는 일별 누적 데이터 이므로 월별 조회시 해당월의 마지막 날짜로 조회하고
        일별은 현재일 이전 7일로 조회한다.
         */
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        do {
            dateList.add(fromZonedDateTime.format(formatter));

            fromZonedDateTime = periodType.plus(fromZonedDateTime);

            if (periodType == MONTHLY) {
                fromZonedDateTime = KoreanDateTimeUtil.toEndDateTimeOfMonth(fromZonedDateTime);
            }
        }
        while (!fromZonedDateTime.isAfter(toZonedDateTime));
        if (periodType == MONTHLY) {
            dateList.add(toZonedDateTime.format(formatter));
        }
        return dateList;
    }

    private static List<String> getDateListForAgeAndGender(DashboardStatisticsPeriodType periodType, String from, String to) {
        //TODO 확인필요 성별 연령별은 전일,전월비교를 위해서 to 일자의 전일을 from 으로 사용한다.
        ZonedDateTime fromZonedDateTime = getEndDateTime(periodType, from);
        ZonedDateTime toZonedDateTime = getYesterdayOrEndDateOfMonth(periodType, to);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return List.of(fromZonedDateTime.format(formatter), toZonedDateTime.format(formatter));
    }

    public static DashboardStatisticsType fromValue(String type) {
        try {
            return DashboardStatisticsType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 DashboardStatisticsType '%s' 타입입니다.".formatted(type));
        }
    }

    private static ZonedDateTime getEndDateTime(DashboardStatisticsPeriodType periodType, String dateString) {
        if (periodType == DAILY) {
            return formattedKoreaZonedDateTime(periodType, dateString);
        }
        return KoreanDateTimeUtil.toEndDateTimeOfMonth(formattedKoreaZonedDateTime(periodType, dateString));
    }

    private static ZonedDateTime getStartDateTime(DashboardStatisticsPeriodType periodType, String dateString) {
        if (periodType == DAILY) {
            return formattedKoreaZonedDateTime(periodType, dateString);
        }
        return KoreanDateTimeUtil.toStartDateTimeOfMonth(formattedKoreaZonedDateTime(periodType, dateString));
    }

    private static ZonedDateTime getYesterdayOrEndDateOfMonth(DashboardStatisticsPeriodType periodType, String dateString) {
        ZonedDateTime toZonedDateTime = formattedKoreaZonedDateTime(periodType, dateString);

        if (periodType == DAILY || KoreanDateTimeUtil.isSameYearAndMonth(toZonedDateTime)) {
            return KoreanDateTimeUtil.getYesterdayZonedDateTime().truncatedTo(ChronoUnit.DAYS);
        }

        return KoreanDateTimeUtil.toEndDateTimeOfMonth(toZonedDateTime).truncatedTo(ChronoUnit.DAYS);
    }

    private static ZonedDateTime formattedKoreaZonedDateTime(DashboardStatisticsPeriodType periodType, String koreanDate) {
        if (periodType == MONTHLY) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            YearMonth yearMonth = YearMonth.parse(koreanDate, formatter);
            return yearMonth.atDay(1).atStartOfDay(ZoneIdUtil.getSeoulZoneId());
        }

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");
        return ZonedDateTime.parse(koreanDate + " 00:00:00+09:00", formatter);
    }

    private static List<DashboardStatisticsType> getMonthlyTypes() {
        final EnumSet<DashboardStatisticsType> types = EnumSet.allOf(DashboardStatisticsType.class);
        types.remove(DashboardStatisticsType.MONTHLY_ACTIVE_USER);
        types.remove(DashboardStatisticsType.DAILY_USER_GENDER_COUNT);
        types.remove(DashboardStatisticsType.DAILY_USER_AGE_COUNT);
        return types.stream().toList();
    }

    public static boolean isMonthlyType(DashboardStatisticsType type) {
        return getMonthlyTypes().contains(type);
    }

    public abstract List<String> getDateList(DashboardStatisticsPeriodType periodType, String from, String to);
}
