package ag.act.service.solidarity.election;

import ag.act.dto.SolidarityLeaderApplicantDto;
import ag.act.dto.SolidarityLeaderApplicationDto;
import ag.act.dto.solidarity.SolidarityLeaderElectionApplyDto;
import ag.act.entity.Solidarity;
import ag.act.entity.solidarity.election.LeaderElectionCurrentDateTimeProvider;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.exception.BadRequestException;
import ag.act.module.solidarity.election.SolidarityLeaderElectionPushUnregister;
import ag.act.repository.solidarity.election.SolidarityLeaderElectionRepository;
import ag.act.service.solidarity.SolidarityService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus.COMPLETE;
import static ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus.SAVE;

@RequiredArgsConstructor
@Service
@Transactional
public class SolidarityLeaderElectionService {

    private static final int NO_CANDIDATE_COUNT = 0;
    private final SolidarityLeaderElectionRepository solidarityLeaderElectionRepository;
    private final SolidarityLeaderApplicantService solidarityLeaderApplicantService;
    private final SolidarityLeaderElectionCreateService solidarityLeaderElectionCreateService;
    private final SolidarityService solidarityService;
    private final SolidarityLeaderElectionPushUnregister solidarityLeaderElectionPushUnregister;

    public void createSolidarityLeaderApplicant(SolidarityLeaderElectionApplyDto applyDto) {
        final SolidarityLeaderElection solidarityLeaderElection = createOrGetSolidarityLeaderElection(applyDto);
        applyDto.setSolidarityLeaderElectionId(solidarityLeaderElection.getId());
        applyDto.setElectionStatus(solidarityLeaderElection.getElectionStatus());

        validateApplyPeriod(startElectionIfApplicable(applyDto.getApplyStatus(), solidarityLeaderElection));

        // 새로운 지원서 제출 (임시 저장 or 제출)
        solidarityLeaderApplicantService.createSolidarityLeaderApplicant(applyDto);

        registerCandidate(applyDto, solidarityLeaderElection);
    }

    public void updateSolidarityLeaderApplicant(SolidarityLeaderElectionApplyDto applyDto) {
        final SolidarityLeaderElection solidarityLeaderElection = getSolidarityLeaderElection(applyDto.getSolidarityLeaderElectionId());
        applyDto.setSolidarityLeaderElectionId(solidarityLeaderElection.getId());
        applyDto.setElectionStatus(solidarityLeaderElection.getElectionStatus());
        validateApplyPeriod(startElectionIfApplicable(applyDto.getApplyStatus(), solidarityLeaderElection));

        solidarityLeaderApplicantService.updateSolidarityLeaderApplicant(applyDto);

        registerCandidate(applyDto, solidarityLeaderElection);
    }

    private void registerCandidate(
        SolidarityLeaderElectionApplyDto applyDto,
        SolidarityLeaderElection election
    ) {
        if (applyDto.getApplyStatus() == COMPLETE) {
            updateCandidateCount(election);
        }
    }

    private void updateCandidateCount(SolidarityLeaderElection election) {
        int completedApplicationsCount = countApplicantsBySolidarityElectionIdAndStatus(election.getId(), COMPLETE);
        election.setCandidateCount(completedApplicationsCount);
    }

    private SolidarityLeaderElection createOrGetSolidarityLeaderElection(SolidarityLeaderElectionApplyDto applyDto) {
        final String stockCode = applyDto.getStockCode();

        return findActiveSolidarityLeaderElection(stockCode)
            .orElseGet(() -> createPendingSolidarityLeaderElection(stockCode));
    }

    private int countApplicantsBySolidarityElectionIdAndStatus(
        Long solidarityLeaderElectionId,
        SolidarityLeaderElectionApplyStatus applyStatus
    ) {
        return solidarityLeaderApplicantService.countApplicantsByIdAndStatus(solidarityLeaderElectionId, applyStatus);
    }

    private SolidarityLeaderElection createPendingSolidarityLeaderElection(String stockCode) {
        return solidarityLeaderElectionCreateService.createPendingSolidarityLeaderElection(stockCode);
    }

    private SolidarityLeaderElection startElectionIfApplicable(
        SolidarityLeaderElectionApplyStatus applyStatus,
        SolidarityLeaderElection solidarityLeaderElection
    ) {
        return isStartSolidarityLeaderElectionRequired(applyStatus, solidarityLeaderElection)
            ? solidarityLeaderElectionCreateService.startSolidarityLeaderElection(solidarityLeaderElection)
            : solidarityLeaderElection;
    }

    private boolean isStartSolidarityLeaderElectionRequired(
        SolidarityLeaderElectionApplyStatus applyStatus,
        SolidarityLeaderElection solidarityLeaderElection
    ) {
        return applyStatus == COMPLETE && solidarityLeaderElection.isPendingElection();
    }

    public SolidarityLeaderElection getSolidarityLeaderElection(Long solidarityLeaderElectionId) {
        return solidarityLeaderElectionRepository.findById(solidarityLeaderElectionId)
            .orElseThrow(() -> new BadRequestException("주주대표 선출 정보를 찾을 수 없습니다."));
    }

    public List<SolidarityLeaderApplicantDto> getSolidarityLeaderApplicants(String stockCode, Long solidarityLeaderElectionId) {
        final Solidarity solidarity = solidarityService.getSolidarityByStockCode(stockCode);
        return solidarityLeaderApplicantService.getSolidarityLeaderApplicants(solidarity.getId(), solidarityLeaderElectionId);
    }

    public SolidarityLeaderApplicationDto getApplication(
        Long solidarityLeaderElectionId,
        Long solidarityLeaderApplicantId
    ) {
        return solidarityLeaderApplicantService.getSolidarityLeaderApplicationDto(
            solidarityLeaderElectionId,
            solidarityLeaderApplicantId
        );
    }

    public Optional<SolidarityLeaderElection> findSolidarityLeaderElection(Long solidarityLeaderElectionId) {
        return solidarityLeaderElectionRepository.findById(solidarityLeaderElectionId);
    }

    public Optional<SolidarityLeaderElection> findSolidarityLeaderElectionByPostId(Long postId) {
        return solidarityLeaderElectionRepository.findByPostId(postId);
    }

    public Optional<SolidarityLeaderElection> findActiveSolidarityLeaderElection(String stockCode) {
        return solidarityLeaderElectionRepository
            .findByStockCodeAndElectionStatusIn(stockCode, SolidarityLeaderElectionStatus.getActiveStatus());
    }

    public Optional<SolidarityLeaderElection> findOnGoingSolidarityLeaderElection(String stockCode) {
        return solidarityLeaderElectionRepository
            .findByStockCodeAndElectionStatusIn(stockCode, SolidarityLeaderElectionStatus.getOngoingStatus());
    }

    public Optional<SolidarityLeaderElection> findVoteClosingDateTimePlusOneDaySolidarityLeaderElection(
        String stockCode
    ) {
        return solidarityLeaderElectionRepository
            .findByStockCodeAndVoteClosingDateTimeAndElectionStatusIn(
                stockCode,
                List.of(
                    SolidarityLeaderElectionStatus.CANDIDATE_REGISTRATION_PERIOD,
                    SolidarityLeaderElectionStatus.VOTE_PERIOD,
                    SolidarityLeaderElectionStatus.FINISHED
                )
            );
    }

    public boolean existsOngoingSolidarityLeaderElection(String stockCode) {
        return solidarityLeaderElectionRepository.existsByStockCodeAndElectionStatusIn(
            stockCode,
            SolidarityLeaderElectionStatus.getOngoingStatus()
        );
    }

    private void validateApplyPeriod(SolidarityLeaderElection solidarityLeaderElection) {
        if (solidarityLeaderElection.isPendingElection()) {
            return;
        }

        if (!solidarityLeaderElection.isCandidateRegistrationPeriod()) {
            throw new BadRequestException("주주대표 지원 기간이 아닙니다.",
                new IllegalArgumentException(
                    "LUCAS_TEST_currentDateTime: " + LeaderElectionCurrentDateTimeProvider.get()
                        + "\nLUCAS_TEST_CandidateRegistrationStartDateTime: " + solidarityLeaderElection.getCandidateRegistrationStartDateTime()
                        + "\nLUCAS_TEST_CandidateRegistrationEndDateTime: " + solidarityLeaderElection.getCandidateRegistrationEndDateTime()
                ));
        }
    }

    public boolean isSolidarityLeaderElectionStatusNotInPreVoteStatus(Long solidarityElectionId) {
        return solidarityLeaderElectionRepository
            .findByIdAndElectionStatusIn(solidarityElectionId, SolidarityLeaderElectionStatus.getPreVoteStatus())
            .isEmpty();
    }

    public void withdrawSolidarityLeaderApplicant(Long solidarityLeaderElectionId, Long solidarityLeaderApplicantId) {
        SolidarityLeaderElection solidarityLeaderElection = getSolidarityLeaderElection(solidarityLeaderElectionId);
        validateWithdraw(solidarityLeaderElection);

        solidarityLeaderApplicantService.withdrawLeaderApplicant(
            solidarityLeaderElection.getId(),
            solidarityLeaderApplicantId,
            SolidarityLeaderElectionApplyStatus.DELETED_BY_USER
        );

        updateCandidateCount(solidarityLeaderElection);

        finishElection(solidarityLeaderElection);
    }

    private void finishElection(SolidarityLeaderElection solidarityLeaderElection) {
        final SolidarityLeaderElectionApplyStatus targetApplyStatus = solidarityLeaderElection.isPendingElection() ? SAVE : COMPLETE;

        final int applicantCount = countApplicantsBySolidarityElectionIdAndStatus(solidarityLeaderElection.getId(), targetApplyStatus);

        if (applicantCount > NO_CANDIDATE_COUNT) {
            return;
        }

        solidarityLeaderElection.finishElectionWithNoCandidate();
        solidarityLeaderElectionPushUnregister.unregister(solidarityLeaderElection);
    }

    private void validateWithdraw(SolidarityLeaderElection solidarityLeaderElection) {
        if (solidarityLeaderElection.isPendingElection() || solidarityLeaderElection.isCandidateRegistrationPeriod()) {
            return;
        }

        throw new BadRequestException("투표 기간에는 지원을 취소 할 수 없습니다.");
    }

    public List<SolidarityLeaderElection> getAllActiveSolidarityLeaderElections() {
        return solidarityLeaderElectionRepository.findAllByElectionStatusIn(SolidarityLeaderElectionStatus.getActiveStatus());
    }

    public SolidarityLeaderElection save(SolidarityLeaderElection solidarityLeaderElection) {
        return solidarityLeaderElectionRepository.save(solidarityLeaderElection);
    }

    public Optional<SolidarityLeaderApplicationDto> findLatestApplication(String stockCode, Long userId) {
        Optional<SolidarityLeaderElection> activeElection = findActiveSolidarityLeaderElection(stockCode);

        final boolean hasApplied = activeElection
            .map(election -> solidarityLeaderApplicantService.existsAppliedApplication(election.getId(), userId))
            .orElse(false);

        if (hasApplied) {
            return Optional.empty();
        }

        return solidarityLeaderApplicantService.findLatestSolidarityLeaderApplicationByStockCodeAndUserId(stockCode, userId);
    }
}
