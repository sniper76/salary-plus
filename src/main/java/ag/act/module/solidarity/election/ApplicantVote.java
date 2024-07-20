package ag.act.module.solidarity.election;

import ag.act.dto.election.ApplicantPollAnswerData;

public record ApplicantVote(
    ApplicantPollAnswerData applicantPollAnswerData,
    Long approvalQuantity,
    Long rejectionQuantity
) {
    public Long applicantId() {
        return applicantPollAnswerData.applicantId();
    }

    public String stockCode() {
        return applicantPollAnswerData.stockCode();
    }
}
