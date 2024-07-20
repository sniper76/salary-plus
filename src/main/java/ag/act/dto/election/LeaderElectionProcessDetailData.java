package ag.act.dto.election;

import java.time.LocalDateTime;

public record LeaderElectionProcessDetailData(
    String title,
    Long value,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String unit
) {
    public static LeaderElectionProcessDetailData withDateTimes(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return new LeaderElectionProcessDetailData(
            null,
            null,
            startDateTime,
            endDateTime,
            null
        );
    }
}
