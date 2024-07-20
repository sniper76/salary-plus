package ag.act.dto.election;

import java.util.List;

public record ApplicantPollAnswerData(
    long applicantId,
    String stockCode,
    String nickname,
    long totalStockQuantity,
    SolidarityLeaderElectionApplicantDataLabel resolutionConditionData,
    SolidarityLeaderElectionApplicantDataLabel earlyFinishedConditionData,
    List<PollAnswerData> pollAnswerDataList
) {
}
