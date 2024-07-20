package ag.act.module.solidarity.election.assigner;

import ag.act.dto.election.ApplicantPollAnswerData;
import ag.act.dto.election.PollAnswerData;
import ag.act.module.solidarity.election.ApplicantVote;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ApplicantVoteConverter {

    public ApplicantVote convert(ApplicantPollAnswerData applicantPollAnswerData) {
        final List<PollAnswerData> pollAnswerDataList = applicantPollAnswerData.pollAnswerDataList();

        return new ApplicantVote(
            applicantPollAnswerData,
            pollAnswerDataList.stream().filter(this::isApproval).mapToLong(PollAnswerData::quantity).sum(),
            pollAnswerDataList.stream().filter(this::isRejection).mapToLong(PollAnswerData::quantity).sum()
        );
    }

    private boolean isApproval(PollAnswerData pollAnswerData) {
        return pollAnswerData.answerType().isApproval();
    }

    private boolean isRejection(PollAnswerData pollAnswerData) {
        return pollAnswerData.answerType().isRejection();
    }
}
