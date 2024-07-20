package ag.act.dto.election;

import ag.act.enums.solidarity.election.SolidarityLeaderElectionAnswerType;

public record PollAnswerData(
    long pollItemId,
    int voteCount,
    long quantity,
    long percentage,
    SolidarityLeaderElectionAnswerType answerType,
    boolean isVoted
) {
    public String getAnswerTypeDisplayName() {
        return answerType.getDisplayName();
    }

    public String getAnswerTypeName() {
        return answerType.name();
    }
}
