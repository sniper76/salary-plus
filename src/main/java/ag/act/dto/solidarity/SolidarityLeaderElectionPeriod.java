package ag.act.dto.solidarity;

import java.time.LocalDateTime;

public record SolidarityLeaderElectionPeriod(
    LocalDateTime candidateRegistrationStartDateTime,
    LocalDateTime candidateRegistrationEndDateTime,
    LocalDateTime voteStartDateTime,
    LocalDateTime voteEndDateTime
) {
}
