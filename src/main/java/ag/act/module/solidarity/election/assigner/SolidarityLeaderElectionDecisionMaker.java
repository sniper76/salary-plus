package ag.act.module.solidarity.election.assigner;

import ag.act.dto.election.ApplicantPollAnswerData;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.facade.election.SolidarityLeaderElectionPostPollFacade;
import ag.act.module.solidarity.election.ApplicantVote;
import ag.act.module.solidarity.election.ISolidarityLeaderElection;
import ag.act.module.solidarity.election.assigner.predicate.ElectedOver25PercentPredicate;
import ag.act.module.solidarity.election.assigner.predicate.ElectedOver50PercentPredicate;
import ag.act.module.solidarity.election.assigner.predicate.ElectedPredicate;
import ag.act.module.solidarity.election.assigner.tiebreaker.LeaderElectionTieBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class SolidarityLeaderElectionDecisionMaker implements ISolidarityLeaderElection.ElectionCondition {
    private static final int FIRST_INDEX = 0;
    private final LeaderElectionTieBreaker leaderElectionTieBreaker;
    private final ApplicantVoteConverter applicantVoteConverter;
    private final SolidarityLeaderElectionPostPollFacade solidarityLeaderElectionPostPollFacade;
    private final ElectedOver50PercentPredicate electedOver50PercentPredicate;
    private final ElectedOver25PercentPredicate electedOver25PercentPredicate;

    private boolean hasNoPostAndPoll(SolidarityLeaderElection solidarityLeaderElection) {
        return solidarityLeaderElection.getPostId() == null;
    }

    private List<ApplicantPollAnswerData> getApplicantPollAnswerDataList(SolidarityLeaderElection solidarityLeaderElection) {
        return solidarityLeaderElectionPostPollFacade.getApplicantPollAnswerDataMap(solidarityLeaderElection)
            .values()
            .stream()
            .toList();
    }

    public Optional<ApplicantPollAnswerData> findEarlyWinner(SolidarityLeaderElection solidarityLeaderElection) {
        if (hasNoPostAndPoll(solidarityLeaderElection)) {
            return Optional.empty();
        }
        return findWinner(solidarityLeaderElection, electedOver50PercentPredicate);
    }

    public Optional<ApplicantPollAnswerData> findWinner(SolidarityLeaderElection solidarityLeaderElection) {
        if (hasNoPostAndPoll(solidarityLeaderElection)) {
            return Optional.empty();
        }
        return findWinner(solidarityLeaderElection, electedOver25PercentPredicate);
    }

    private Optional<ApplicantPollAnswerData> findWinner(SolidarityLeaderElection solidarityLeaderElection, ElectedPredicate electedPredicate) {

        final List<ApplicantPollAnswerData> applicantPollAnswerDataList = getApplicantPollAnswerDataList(solidarityLeaderElection);

        if (isEmptyApplicantAnswers(applicantPollAnswerDataList)) {
            return Optional.empty();
        }

        final List<ApplicantVote> eligibleApplicantVoteList = getEligibleApplicantVoteList(
            applicantPollAnswerDataList,
            solidarityLeaderElection.getTotalStockQuantity(),
            electedPredicate
        );

        if (isEmptyEligibleApplicantVotes(eligibleApplicantVoteList)) {
            return Optional.empty();
        }

        return findTopOneApplicantPollAnswerData(
            eligibleApplicantVoteList,
            getHighestApprovalQuantity(eligibleApplicantVoteList)
        );
    }

    private Optional<ApplicantPollAnswerData> findTopOneApplicantPollAnswerData(
        List<ApplicantVote> eligibleApplicantVoteList,
        long highestApprovalQuantity
    ) {
        final List<ApplicantVote> topApplicantVoteList = eligibleApplicantVoteList.stream()
            .filter(applicantVotes -> applicantVotes.approvalQuantity() == highestApprovalQuantity)
            .toList();

        if (hasOnlyOneItem(topApplicantVoteList)) {
            return Optional.of(topApplicantVoteList.get(FIRST_INDEX).applicantPollAnswerData());
        }

        return Optional.of(leaderElectionTieBreaker.breakTie(topApplicantVoteList));
    }

    private List<ApplicantVote> getEligibleApplicantVoteList(
        List<ApplicantPollAnswerData> applicantPollAnswerDataList,
        long totalStockQuantity,
        ElectedPredicate electedPredicate
    ) {
        return applicantPollAnswerDataList
            .stream()
            .map(applicantVoteConverter::convert)
            .sorted(Comparator.comparing(ApplicantVote::approvalQuantity).reversed())
            .filter(applicantVotes -> electedPredicate.test(totalStockQuantity, applicantVotes))
            .toList();
    }

    private boolean isEmptyEligibleApplicantVotes(List<ApplicantVote> eligibleApplicantVoteList) {
        return eligibleApplicantVoteList.isEmpty();
    }

    private boolean isEmptyApplicantAnswers(List<ApplicantPollAnswerData> applicantPollAnswerDataList) {
        return applicantPollAnswerDataList.isEmpty();
    }

    private boolean hasOnlyOneItem(List<ApplicantVote> topApplicantVoteList) {
        return topApplicantVoteList.size() == 1;
    }

    private Long getHighestApprovalQuantity(List<ApplicantVote> eligibleApplicantVoteList) {
        return eligibleApplicantVoteList.get(FIRST_INDEX).approvalQuantity();
    }
}
