package ag.act.converter.election;

import ag.act.converter.election.vote.SolidarityLeaderElectionApplicantDataResponseConverter;
import ag.act.dto.election.ApplicantPollAnswerData;
import ag.act.dto.election.PollAnswerData;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.model.SolidarityLeaderElectionApplicantDataResponse;
import ag.act.model.SolidarityLeaderElectionDetailResponse;
import ag.act.model.SolidarityLeaderElectionVoteItemDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class SolidarityLeaderElectionVoteItemsResponseConverter {
    private final SolidarityLeaderElectionApplicantDataResponseConverter solidarityLeaderElectionApplicantDataResponseConverter;

    public SolidarityLeaderElectionDetailResponse convert(
        SolidarityLeaderElection solidarityLeaderElection,
        Map<Long, ApplicantPollAnswerData> applicantPollAnswerDataMap
    ) {
        final List<SolidarityLeaderElectionApplicantDataResponse> applicantsResponse = getApplicantsResponse(applicantPollAnswerDataMap);

        return new SolidarityLeaderElectionDetailResponse()
            .solidarityLeaderElectionId(solidarityLeaderElection.getId())
            .totalVoterCount(getTotalVoterCount(applicantPollAnswerDataMap))
            .status(solidarityLeaderElection.getElectionStatus().name())
            .isVoted(isVoted(applicantsResponse))
            .pollApplicants(applicantsResponse);
    }

    private boolean isVoted(List<SolidarityLeaderElectionApplicantDataResponse> applicantsResponse) {
        return applicantsResponse.stream()
            .map(SolidarityLeaderElectionApplicantDataResponse::getPollItemGroups)
            .flatMap(List::stream)
            .anyMatch(SolidarityLeaderElectionVoteItemDataResponse::getIsVoted);
    }

    private int getTotalVoterCount(Map<Long, ApplicantPollAnswerData> applicantPollAnswerDataMap) {
        final Collection<ApplicantPollAnswerData> pollAnswers = applicantPollAnswerDataMap.values();
        final int candidateCount = pollAnswers.size();

        if (candidateCount == 0) {
            return 0;
        }

        return pollAnswers
            .stream()
            .map(ApplicantPollAnswerData::pollAnswerDataList)
            .flatMap(List::stream)
            .mapToInt(PollAnswerData::voteCount)
            .sum() / candidateCount;
    }

    private List<SolidarityLeaderElectionApplicantDataResponse> getApplicantsResponse(
        Map<Long, ApplicantPollAnswerData> applicantPollAnswerDataMap
    ) {
        return applicantPollAnswerDataMap
            .values()
            .stream()
            .map(solidarityLeaderElectionApplicantDataResponseConverter)
            .toList();
    }
}
