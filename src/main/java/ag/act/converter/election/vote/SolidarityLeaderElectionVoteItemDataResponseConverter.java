package ag.act.converter.election.vote;


import ag.act.converter.Converter;
import ag.act.dto.election.PollAnswerData;
import ag.act.model.SolidarityLeaderElectionVoteItemDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SolidarityLeaderElectionVoteItemDataResponseConverter
    implements Converter<PollAnswerData, SolidarityLeaderElectionVoteItemDataResponse> {

    public SolidarityLeaderElectionVoteItemDataResponse convert(PollAnswerData pollAnswerData) {
        return new SolidarityLeaderElectionVoteItemDataResponse()
            .pollItemId(pollAnswerData.pollItemId())
            .isVoted(pollAnswerData.isVoted())
            .title(pollAnswerData.getAnswerTypeDisplayName())
            .type(pollAnswerData.getAnswerTypeName())
            .stockQuantity(pollAnswerData.quantity())
            .stockQuantityPercentage(pollAnswerData.percentage())
            .voteCount(pollAnswerData.voteCount());
    }

    @Override
    public SolidarityLeaderElectionVoteItemDataResponse apply(PollAnswerData pollAnswerData) {
        return convert(pollAnswerData);
    }
}
