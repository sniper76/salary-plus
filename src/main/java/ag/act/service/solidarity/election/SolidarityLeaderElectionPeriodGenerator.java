package ag.act.service.solidarity.election;

import ag.act.dto.solidarity.SolidarityLeaderElectionPeriod;
import ag.act.entity.solidarity.election.LeaderElectionCurrentDateTimeProvider;
import ag.act.enums.SolidarityLeaderElectionProcedure;
import ag.act.util.KoreanDateTimeUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class SolidarityLeaderElectionPeriodGenerator {

    @SuppressWarnings("UnnecessaryLocalVariable")
    public SolidarityLeaderElectionPeriod generate() {

        final LocalDateTime candidateRegistrationStartDateTime = LeaderElectionCurrentDateTimeProvider.get();
        final LocalDateTime candidateRegistrationEndDateTime = getCandidateRegistrationEndDateTimeInUtc();
        final LocalDateTime voteStartDateTime = candidateRegistrationEndDateTime;
        final LocalDateTime voteEndDateTime = voteStartDateTime.plusDays(SolidarityLeaderElectionProcedure.START_VOTING.getDurationDays());

        return new SolidarityLeaderElectionPeriod(
            candidateRegistrationStartDateTime,
            candidateRegistrationEndDateTime,
            voteStartDateTime,
            voteEndDateTime
        );
    }

    private LocalDateTime getCandidateRegistrationEndDateTimeInUtc() {
        final LocalDate todayLocalDate = KoreanDateTimeUtil.getTodayLocalDate();

        return KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDate(todayLocalDate)
            .plusDays(SolidarityLeaderElectionProcedure.RECRUIT_APPLICANTS.getDurationDays());
    }
}
