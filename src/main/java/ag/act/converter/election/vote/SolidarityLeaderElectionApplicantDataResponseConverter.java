package ag.act.converter.election.vote;


import ag.act.converter.Converter;
import ag.act.dto.election.ApplicantPollAnswerData;
import ag.act.dto.election.PollAnswerData;
import ag.act.model.SolidarityLeaderElectionApplicantDataResponse;
import ag.act.model.SolidarityLeaderElectionVoteItemDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class SolidarityLeaderElectionApplicantDataResponseConverter
    implements Converter<ApplicantPollAnswerData, SolidarityLeaderElectionApplicantDataResponse> {

    private final SolidarityLeaderElectionVoteItemDataResponseConverter solidarityLeaderElectionVoteItemDataResponseConverter;

    public SolidarityLeaderElectionApplicantDataResponse convert(ApplicantPollAnswerData applicantPollAnswerData) {
        return new SolidarityLeaderElectionApplicantDataResponse()
            .solidarityLeaderApplicantId(applicantPollAnswerData.applicantId())
            .nickname(applicantPollAnswerData.nickname())
            .totalVoteStockQuantity(applicantPollAnswerData.totalStockQuantity())
            .resolutionCondition(applicantPollAnswerData.resolutionConditionData().toResponse())
            .finishedEarlyCondition(applicantPollAnswerData.earlyFinishedConditionData().toResponse())
            .pollItemGroups(toPollItemGroups(applicantPollAnswerData.pollAnswerDataList()));
    }

    @Override
    public SolidarityLeaderElectionApplicantDataResponse apply(ApplicantPollAnswerData applicantPollAnswerData) {
        return convert(applicantPollAnswerData);
    }

    private List<SolidarityLeaderElectionVoteItemDataResponse> toPollItemGroups(List<PollAnswerData> pollAnswerDataList) {
        return pollAnswerDataList
            .stream()
            .map(solidarityLeaderElectionVoteItemDataResponseConverter)
            .toList();
    }
}
