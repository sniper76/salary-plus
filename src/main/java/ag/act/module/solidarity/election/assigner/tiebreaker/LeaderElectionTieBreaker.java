package ag.act.module.solidarity.election.assigner.tiebreaker;

import ag.act.dto.election.ApplicantPollAnswerData;
import ag.act.module.solidarity.election.ApplicantVote;
import ag.act.module.solidarity.election.assigner.tiebreaker.filter.MostNumberOfPostsApplicantVoteFilter;
import ag.act.module.solidarity.election.assigner.tiebreaker.filter.OldestUserRegistrationDateApplicantVoteFilter;
import ag.act.module.solidarity.election.assigner.tiebreaker.filter.TieBreakerFilter;
import ag.act.module.solidarity.election.assigner.tiebreaker.filter.TopStockQuantityApplicantVoteFilter;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Transactional
@Component
@Slf4j
public class LeaderElectionTieBreaker {

    private final List<TieBreakerFilter> tieBreakerFilters;

    public LeaderElectionTieBreaker(
        TopStockQuantityApplicantVoteFilter topStockQuantityApplicantVoteFilter,
        OldestUserRegistrationDateApplicantVoteFilter oldestUserRegistrationDateApplicantVoteFilter,
        MostNumberOfPostsApplicantVoteFilter mostNumberOfPostsApplicantVoteFilter
    ) {
        this.tieBreakerFilters = List.of(
            topStockQuantityApplicantVoteFilter,
            oldestUserRegistrationDateApplicantVoteFilter,
            mostNumberOfPostsApplicantVoteFilter
        );
    }

    public ApplicantPollAnswerData breakTie(List<ApplicantVote> tieApplicantVoteList) {
        final TieBreakerResultHolder resultHolder = new TieBreakerResultHolder(tieApplicantVoteList);

        return tieBreakerFilters.stream()
            .peek(tieBreakerFilter -> resultHolder.setTempResult(tieBreakerFilter.filter(resultHolder.getTempResult())))
            .filter(tieBreakerFilter -> resultHolder.hasOnlyOneResult())
            .findFirst()
            .map(applicantVoteList -> resultHolder.getFirstItemOfResult().applicantPollAnswerData())
            .orElseThrow(() -> new IllegalStateException("후보자들의 모든 조건이 동일합니다."));
    }
}
