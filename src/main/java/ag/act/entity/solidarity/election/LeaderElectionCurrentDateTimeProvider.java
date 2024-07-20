package ag.act.entity.solidarity.election;

import ag.act.util.KoreanDateTimeUtil;
import ag.act.util.ZoneIdUtil;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class LeaderElectionCurrentDateTimeProvider {

    private LeaderElectionCurrentDateTimeProvider() {
    }

    public static LocalDateTime get() {
        return LocalDateTime.now();
    }

    public static ZonedDateTime getKoreanDateTime() {
        return ZonedDateTime.now(ZoneIdUtil.getSeoulZoneId());
    }

    public static LocalDateTime toKoreanTimeUntilMidnightNextDay(LocalDateTime voteClosingDateTime) {
        return KoreanDateTimeUtil.toKoreanTimeUntilMidnightNextDay(voteClosingDateTime);
    }
}
