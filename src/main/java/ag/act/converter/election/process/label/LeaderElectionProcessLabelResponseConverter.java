package ag.act.converter.election.process.label;

import ag.act.entity.solidarity.election.LeaderElectionCurrentDateTimeProvider;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.model.LeaderElectionProcessLabelResponse;
import ag.act.module.solidarity.election.ISolidarityLeaderElection;
import ag.act.util.KoreanDateTimeUtil;
import jakarta.annotation.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public interface LeaderElectionProcessLabelResponseConverter extends ISolidarityLeaderElection.ElectionDetailLabel {

    double HOURS_A_DAY = 24.0;

    LeaderElectionProcessLabelResponse convert(SolidarityLeaderElection leaderElection);

    boolean supports(SolidarityLeaderElectionStatus status);

    @Nullable
    default LeaderElectionProcessLabelResponse toResponse(String title, String color) {
        return new LeaderElectionProcessLabelResponse()
            .title(title)
            .color(color);
    }

    default LeaderElectionProcessLabelResponse getProcessStepLabelResponse(
        LocalDateTime endDateTime,
        boolean isProcessingStepPeriod,
        boolean isFinishedStepPeriod
    ) {
        if (isFinishedStepPeriod) {
            return toResponse(CLOSING_LABEL, COLOR_FOR_COMPLETE);
        }
        if (isProcessingStepPeriod) {
            final LocalDateTime convertedEndDateTime = adjustEndDateTime(endDateTime);
            final LocalDateTime convertedCurrentDateTime = LeaderElectionCurrentDateTimeProvider.getKoreanDateTime().toLocalDateTime();
            final int days = (int) Math.ceil(Duration.between(convertedCurrentDateTime, convertedEndDateTime).toHours() / HOURS_A_DAY);
            final String title = days == 1 ? "오늘 마감" : "%s일 남음".formatted(days);
            return toResponse(title, COLOR_FOR_PROCESSING);
        }
        return null;
    }

    private LocalDateTime adjustEndDateTime(LocalDateTime endDateTime) {
        final ZonedDateTime koreanEndDateTime = KoreanDateTimeUtil.toKoreanTime(endDateTime);

        if (koreanEndDateTime.toLocalTime().isAfter(LocalTime.MIDNIGHT)) {
            return koreanEndDateTime.plusDays(1).truncatedTo(ChronoUnit.DAYS).toLocalDateTime();
        } else {
            return koreanEndDateTime.truncatedTo(ChronoUnit.DAYS).toLocalDateTime();
        }
    }
}
