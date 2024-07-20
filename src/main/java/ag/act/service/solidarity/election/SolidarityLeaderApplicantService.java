package ag.act.service.solidarity.election;

import ag.act.configuration.security.ActUserProvider;
import ag.act.dto.SolidarityLeaderApplicantDto;
import ag.act.dto.SolidarityLeaderApplicationDto;
import ag.act.dto.solidarity.SolidarityLeaderElectionApplyDto;
import ag.act.dto.user.SolidarityLeaderApplicantUserDto;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.User;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.exception.NotFoundException;
import ag.act.module.solidarity.election.SolidarityLeaderElectionSlackNotifier;
import ag.act.repository.SolidarityLeaderApplicantRepository;
import ag.act.repository.UserRepository;
import ag.act.service.solidarity.SolidarityService;
import ag.act.service.solidarity.election.notifier.SolidarityLeaderApplicantChangeNotifier;
import ag.act.validator.solidarity.ApplySolidarityValidator;
import ag.act.validator.solidarity.SolidarityLeaderApplicationPermissionValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus.COMPLETE;
import static ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus.SAVE;

@SuppressWarnings("LineLength")
@RequiredArgsConstructor
@Service
@Transactional
public class SolidarityLeaderApplicantService {
    private static final int DEFAULT_VERSION = 1;
    private static final int NEW_VERSION = 2;

    private final SolidarityLeaderApplicantRepository solidarityLeaderApplicantRepository;
    private final ApplySolidarityValidator applySolidarityValidator;
    private final SolidarityLeaderApplicationPermissionValidator solidarityLeaderApplicationPermissionValidator;
    private final UserRepository userRepository;
    private final SolidarityService solidarityService;
    private final SolidarityLeaderService solidarityLeaderService;
    private final SolidarityLeaderApplicantChangeNotifier solidarityLeaderApplicantChangeNotifier;
    private final SolidarityLeaderElectionSlackNotifier solidarityLeaderElectionSlackNotifier;

    public Boolean isUserAppliedSolidarity(String stockCode) {
        return findSolidarityLeaderApplicant(stockCode).isPresent();
    }

    //TODO: 주주선출 배포가 성공적으로 완료되면 삭제한다. -> (CMS 해임 + 종목홈 리더 조회에 불필요한 코드로 사용중)
    public Optional<SolidarityLeaderApplicant> findSolidarityLeaderApplicant(String stockCode) {
        User user = ActUserProvider.getNoneNull();
        Solidarity solidarity = solidarityService.getSolidarityByStockCode(stockCode);

        return solidarityLeaderApplicantRepository
            .findBySolidarityIdAndUserIdAndVersion(solidarity.getId(), user.getId(), DEFAULT_VERSION);
    }

    public Optional<SolidarityLeaderApplicant> findSolidarityLeaderApplicant(Long solidarityLeaderApplicantId) {
        return solidarityLeaderApplicantRepository.findById(solidarityLeaderApplicantId);
    }

    public SolidarityLeaderApplicant getSolidarityLeaderApplicant(Long solidarityLeaderApplicantId) {
        return solidarityLeaderApplicantRepository.findById(solidarityLeaderApplicantId)
            .orElseThrow(() -> new NotFoundException("지원서를 임시 저장하거나 제출한 내역이 없습니다."));
    }

    //TODO: 주주선출 배포가 성공적으로 완료되면 삭제한다.
    public void applyForLeader(String stockCode) {
        applySolidarityValidator.validate(stockCode, DEFAULT_VERSION);
        final User user = ActUserProvider.getNoneNull();
        final Solidarity solidarity = solidarityService.getSolidarityByStockCode(stockCode);

        SolidarityLeaderApplicant applicant = new SolidarityLeaderApplicant();
        applicant.setUserId(user.getId());
        applicant.setSolidarityId(solidarity.getId());
        applicant.setVersion(DEFAULT_VERSION);
        applicant.setApplyStatus(COMPLETE);

        solidarityLeaderApplicantRepository.save(applicant);

        solidarityLeaderApplicantChangeNotifier.notifyAppliedLeader(solidarity.getStock().getName(), user.getName());
    }

    public SolidarityLeaderApplicant createSolidarityLeaderApplicant(SolidarityLeaderElectionApplyDto applyDto) {
        applySolidarityValidator.validate(NEW_VERSION, applyDto);

        final User user = ActUserProvider.getNoneNull();
        final Solidarity solidarity = solidarityService.getSolidarityByStockCode(applyDto.getStockCode());

        final SolidarityLeaderApplicant applicant = new SolidarityLeaderApplicant();
        applicant.setUserId(user.getId());
        applicant.setVersion(NEW_VERSION);
        applicant.setSolidarityId(solidarity.getId());

        populateApplicantDetails(applyDto, applicant);

        final SolidarityLeaderApplicant savedApplicant = solidarityLeaderApplicantRepository.save(applicant);

        sendSlackIfFirstComplete(applyDto, solidarity.getId());

        return savedApplicant;
    }

    public void updateSolidarityLeaderApplicant(SolidarityLeaderElectionApplyDto applyDto) {
        final SolidarityLeaderApplicant applicant = getSolidarityLeaderApplicant(applyDto.getSolidarityLeaderApplicantId());

        solidarityLeaderApplicationPermissionValidator.validateEditPermission(applicant);
        applySolidarityValidator.validate(applyDto, applicant);

        final SolidarityLeaderElectionApplyStatus originalStatus = applicant.getApplyStatus();

        populateApplicantDetails(applyDto, applicant);
        updateSolidarityLeaderApplicant(applicant);

        sendSlackIfFirstComplete(applyDto, originalStatus, applicant.getSolidarityId());
    }

    public SolidarityLeaderApplicant updateSolidarityLeaderApplicant(SolidarityLeaderApplicant solidarityLeaderApplicant) {
        return solidarityLeaderApplicantRepository.save(solidarityLeaderApplicant);
    }

    private void sendSlackIfFirstComplete(
        SolidarityLeaderElectionApplyDto applyDto,
        SolidarityLeaderElectionApplyStatus originalStatus,
        Long solidarityId
    ) {
        if (originalStatus == SAVE) {
            sendSlackIfFirstComplete(applyDto, solidarityId);
        }
    }

    private void sendSlackIfFirstComplete(SolidarityLeaderElectionApplyDto applyDto, Long solidarityId) {
        if (applyDto.getApplyStatus() == COMPLETE) {
            solidarityLeaderElectionSlackNotifier.notifyIfApplicantComplete(solidarityId, ActUserProvider.getNoneNull());
        }
    }

    private void populateApplicantDetails(SolidarityLeaderElectionApplyDto applyDto, SolidarityLeaderApplicant applicant) {
        applicant.setReasonsForApplying(applyDto.getReasonsForApply());
        applicant.setKnowledgeOfCompanyManagement(applyDto.getKnowledgeOfCompanyManagement());
        applicant.setGoals(applyDto.getGoals());
        applicant.setCommentsForStockHolder(applyDto.getCommentsForStockHolder());
        applicant.setApplyStatus(applyDto.getApplyStatus());
        applicant.setSolidarityLeaderElectionId(applyDto.getSolidarityLeaderElectionId());
    }

    public void cancelLeaderApplication(String stockCode) {
        final SolidarityLeaderApplicant applicant = getLeaderApplicant(stockCode);

        solidarityLeaderApplicantRepository.delete(applicant);
    }

    public void cancelLeaderApplication(Long solidarityLeaderApplicantId) {
        final SolidarityLeaderApplicant applicant = getLeaderApplicant(solidarityLeaderApplicantId);

        solidarityLeaderApplicantRepository.delete(applicant);
    }

    public SolidarityLeaderApplicant withdrawLeaderApplicant(
        Long solidarityLeaderElectionId,
        Long solidarityLeaderApplicantId,
        SolidarityLeaderElectionApplyStatus applyStatus
    ) {
        final SolidarityLeaderApplicant applicant = getLeaderApplicant(solidarityLeaderElectionId, solidarityLeaderApplicantId);

        solidarityLeaderApplicationPermissionValidator.validateWithdraw(applicant, "작성자만이 지원을 취소할 수 있습니다.");

        applicant.setApplyStatus(applyStatus);
        return updateSolidarityLeaderApplicant(applicant);
    }

    public void electLeaderApplicant(Long solidarityLeaderApplicantId) {
        final SolidarityLeaderApplicant applicant = getLeaderApplicant(solidarityLeaderApplicantId);

        solidarityLeaderService.createSolidarityLeader(applicant);
    }

    private SolidarityLeaderApplicant getLeaderApplicant(String stockCode) {
        return findSolidarityLeaderApplicant(stockCode)
            .orElseThrow(getNotFoundApplicationExceptionSupplier());
    }

    private SolidarityLeaderApplicant getLeaderApplicant(Long solidarityLeaderApplicantId) {
        return solidarityLeaderApplicantRepository.findById(solidarityLeaderApplicantId)
            .orElseThrow(getNotFoundApplicationExceptionSupplier());
    }

    private SolidarityLeaderApplicant getLeaderApplicant(Long solidarityLeaderElectionId, Long solidarityLeaderApplicantId) {
        return solidarityLeaderApplicantRepository.findByIdAndSolidarityLeaderElectionId(solidarityLeaderApplicantId, solidarityLeaderElectionId)
            .orElseThrow(getNotFoundApplicationExceptionSupplier());
    }

    public List<SolidarityLeaderApplicantUserDto> getSolidarityLeaderApplicantUsers(Long solidarityLeaderElectionId) {
        return userRepository.findAllCompletedSolidarityLeaderApplicantUsers(solidarityLeaderElectionId);
    }

    public List<SolidarityLeaderApplicantDto> getSolidarityLeaderApplicants(Long solidarityId, Long solidarityLeaderElectionId) {
        return solidarityLeaderApplicantRepository.findAllBySolidarityIdAndSolidarityLeaderElectionIdOrderByCreatedAtAsc(
            solidarityId,
            solidarityLeaderElectionId
        );
    }

    public SolidarityLeaderApplicationDto getSolidarityLeaderApplicationDto(
        Long solidarityLeaderElectionId,
        Long solidarityLeaderApplicationId
    ) {
        SolidarityLeaderApplicationDto applicationDto = solidarityLeaderApplicantRepository.findBySolidarityApplicationIdAndStockCode(
            solidarityLeaderElectionId,
            solidarityLeaderApplicationId,
            SolidarityLeaderElectionApplyStatus.getApplyStatuses()
        ).orElseThrow(getNotFoundApplicationExceptionSupplier());

        solidarityLeaderApplicationPermissionValidator.validateViewPermission(applicationDto);
        return applicationDto;
    }

    public Optional<SolidarityLeaderApplicationDto> findLatestAppliedSolidarityLeaderApplicantByStockCodeAndUserId(
        String stockCode,
        Long userId
    ) {
        return findLatestSolidarityLeaderApplicantByStockCodeAndUserIdAndStatusIn(
            stockCode,
            userId,
            SolidarityLeaderElectionApplyStatus.getApplyStatuses()
        );
    }

    private Optional<SolidarityLeaderApplicationDto> findLatestSolidarityLeaderApplicantByStockCodeAndUserIdAndStatusIn(
        String stockCode,
        Long userId,
        List<SolidarityLeaderElectionApplyStatus> statuses
    ) {
        return solidarityLeaderApplicantRepository.findLatestSolidarityLeaderApplicantByStockCodeAndUserIdAndVersion(
            stockCode,
            userId,
            NEW_VERSION,
            statuses
        );
    }

    public Optional<SolidarityLeaderApplicationDto> findLatestSolidarityLeaderApplicationByStockCodeAndUserId(String stockCode, Long userId) {
        return findLatestSolidarityLeaderApplicantByStockCodeAndUserIdAndStatusIn(
            stockCode,
            userId,
            SolidarityLeaderElectionApplyStatus.getAllStatuses()
        );
    }

    public boolean isUserAppliedForActiveSolidarityLeaderElection(Long userId) {
        return !getCompletedSolidarityLeaderApplicantsInActiveElection(userId).isEmpty();
    }

    public List<SolidarityLeaderApplicant> getCompletedSolidarityLeaderApplicantsInActiveElection(Long userId) {
        return solidarityLeaderApplicantRepository.findAllActiveAndCompletedSolidarityLeaderApplicantsByUserId(userId);
    }

    public List<SolidarityLeaderApplicant> getSavedSolidarityLeaderApplicantsByElectionId(Long solidarityLeaderElectionId) {
        return solidarityLeaderApplicantRepository.findAllBySolidarityLeaderElectionIdAndApplyStatus(solidarityLeaderElectionId, SAVE);
    }

    @NotNull
    private Supplier<NotFoundException> getNotFoundApplicationExceptionSupplier() {
        return () -> new NotFoundException("해당 종목에 대한 지원 내역이 없습니다.");
    }

    public int countCompletedApplicantsByIdAndStatus(Long solidarityLeaderElectionId) {
        return solidarityLeaderApplicantRepository.countBySolidarityLeaderElectionIdAndApplyStatus(solidarityLeaderElectionId, COMPLETE);
    }

    public int countApplicantsByIdAndStatus(Long solidarityLeaderElectionId, SolidarityLeaderElectionApplyStatus applyStatus) {
        return solidarityLeaderApplicantRepository.countBySolidarityLeaderElectionIdAndApplyStatus(solidarityLeaderElectionId, applyStatus);
    }

    public void updateTemporarySavedApplicantsToExpired(List<SolidarityLeaderApplicant> temporarySavedApplicants) {
        temporarySavedApplicants.stream()
            .map(this::setApplicantApplyStatusExpired)
            .forEach(solidarityLeaderApplicantRepository::save);
    }

    private SolidarityLeaderApplicant setApplicantApplyStatusExpired(SolidarityLeaderApplicant solidarityLeaderApplicant) {
        solidarityLeaderApplicant.setApplyStatus(SolidarityLeaderElectionApplyStatus.EXPIRED);

        return solidarityLeaderApplicant;
    }

    public boolean existsAppliedApplication(Long solidarityLeaderElectionId, Long userId) {
        return solidarityLeaderApplicantRepository.existsBySolidarityLeaderElectionIdAndUserIdAndApplyStatusInAndVersion(
            solidarityLeaderElectionId,
            userId,
            SolidarityLeaderElectionApplyStatus.getApplyStatuses(),
            NEW_VERSION
        );
    }
}
