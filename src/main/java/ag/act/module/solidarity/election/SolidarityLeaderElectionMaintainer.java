package ag.act.module.solidarity.election;

import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.module.solidarity.election.assigner.FinishedEarlySolidarityLeaderAssigner;
import ag.act.module.solidarity.election.assigner.FinishedOnTimeSolidarityLeaderAssigner;
import ag.act.service.solidarity.election.SolidarityLeaderElectionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class SolidarityLeaderElectionMaintainer {

    private final SolidarityLeaderElectionService solidarityLeaderElectionService;
    private final ElectionStatusModifier electionStatusModifier;
    private final TemporarySavedApplicantExpiryUpdater temporarySavedApplicantExpiryUpdater;
    private final TotalVoteStockQuantityModifier totalVoteStockQuantityModifier;
    private final TotalStockQuantityModifier totalStockQuantityModifier;
    private final CandidateCountModifier candidateCountModifier;
    private final SolidarityLeaderElectionPostPollCreator solidarityLeaderElectionPostPollCreator;
    private final FinishedEarlySolidarityLeaderAssigner finishedEarlySolidarityLeaderAssigner;
    private final FinishedOnTimeSolidarityLeaderAssigner finishedOnTimeSolidarityLeaderAssigner;
    private final SolidarityLeaderElectionWithNoCandidateCloser solidarityLeaderElectionWithNoCandidateCloser;
    private final SolidarityLeaderElectionPostStatusUpdater solidarityLeaderElectionPostStatusUpdater;

    public List<SolidarityLeaderElection> getAllActiveSolidarityLeaderElections() {
        return solidarityLeaderElectionService.getAllActiveSolidarityLeaderElections();
    }

    public void maintainSolidarityLeaderElection(SolidarityLeaderElection solidarityLeaderElection) {
        // TODO 후보자가 등록(COMPLETED) 할때마다 슬랙 알림 발송 - "종목명 주주대표 후보자 닉네임 지원" - https://trello.com/c/twX4X02x
        expireTemporarySavedApplicants(solidarityLeaderElection);
        createPostAndPollIfApplicable(solidarityLeaderElection);
        setTotalStockQuantity(solidarityLeaderElection);
        setCandidateCount(solidarityLeaderElection);
        setTotalVoteStockQuantityAndStake(solidarityLeaderElection);
        closeElectionIfNoCandidates(solidarityLeaderElection);
        assignLeader(solidarityLeaderElection);
        updatePostStatus(solidarityLeaderElection);
        setElectionStatus(solidarityLeaderElection);
        saveElection(solidarityLeaderElection);
    }

    private void expireTemporarySavedApplicants(SolidarityLeaderElection solidarityLeaderElection) {
        temporarySavedApplicantExpiryUpdater.expireIfApplicable(solidarityLeaderElection);
    }

    private void updatePostStatus(SolidarityLeaderElection solidarityLeaderElection) {
        solidarityLeaderElectionPostStatusUpdater.updateIfApplicable(solidarityLeaderElection);
    }

    private void setElectionStatus(SolidarityLeaderElection solidarityLeaderElection) {
        electionStatusModifier.changeToVoteStatusIfApplicable(solidarityLeaderElection);
    }

    private void closeElectionIfNoCandidates(SolidarityLeaderElection solidarityLeaderElection) {
        solidarityLeaderElectionWithNoCandidateCloser.closeIfApplicable(solidarityLeaderElection);
    }

    private void createPostAndPollIfApplicable(SolidarityLeaderElection solidarityLeaderElection) {
        solidarityLeaderElectionPostPollCreator.createIfApplicable(solidarityLeaderElection);
    }

    private void setTotalVoteStockQuantityAndStake(SolidarityLeaderElection solidarityLeaderElection) {
        totalVoteStockQuantityModifier.set(solidarityLeaderElection);
        // TODO stake 도 계산을 해서 저장해야 할까? - 사용하는 곳이 없어서 일단 무시한다.
    }

    private void assignLeader(SolidarityLeaderElection solidarityLeaderElection) {
        assignLeaderWhenFinishedOnTime(solidarityLeaderElection);
        assignLeaderWhenFinishedEarly(solidarityLeaderElection);
    }

    private void assignLeaderWhenFinishedEarly(SolidarityLeaderElection solidarityLeaderElection) {
        finishedEarlySolidarityLeaderAssigner.assignLeader(solidarityLeaderElection);
    }

    private void assignLeaderWhenFinishedOnTime(SolidarityLeaderElection solidarityLeaderElection) {
        finishedOnTimeSolidarityLeaderAssigner.assignLeader(solidarityLeaderElection);
    }

    private void setCandidateCount(SolidarityLeaderElection solidarityLeaderElection) {
        candidateCountModifier.setIfApplicable(solidarityLeaderElection);
    }

    private void setTotalStockQuantity(SolidarityLeaderElection solidarityLeaderElection) {
        totalStockQuantityModifier.setIfApplicable(solidarityLeaderElection);
    }

    private void saveElection(SolidarityLeaderElection solidarityLeaderElection) {
        solidarityLeaderElectionService.save(solidarityLeaderElection);
    }
}
