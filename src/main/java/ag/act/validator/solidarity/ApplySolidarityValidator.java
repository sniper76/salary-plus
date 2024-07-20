package ag.act.validator.solidarity;

import ag.act.configuration.security.ActUserProvider;
import ag.act.dto.solidarity.SolidarityLeaderElectionApplyDto;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityDailySummary;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.User;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.exception.BadRequestException;
import ag.act.repository.SolidarityLeaderApplicantRepository;
import ag.act.service.solidarity.SolidarityService;
import ag.act.util.SolidarityLeaderElectionFeatureActiveConditionProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus.COMPLETE;
import static ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus.SAVE;

@AllArgsConstructor
@Component
@Slf4j
public class ApplySolidarityValidator {
    private static final int APPLICATION_ITEM_MIN_THRESHOLD = 80;
    private static final int APPLICATION_ITEM_MAX_THRESHOLD = 500;
    private static final String ALREADY_CREATE_APPLICANT = "주주대표 지원 이력이 존재합니다. 임시저장의 경우 지원서를 수정하여 제출해주세요.";

    private final SolidarityService solidarityService;
    private final SolidarityLeaderApplicantRepository solidarityLeaderApplicantRepository;
    private final SolidarityLeaderElectionFeatureActiveConditionProvider solidarityLeaderElectionFeatureActiveConditionProvider;

    //TODO: 주주선출 배포가 성공적으로 완료되면 삭제한다.
    public void validate(String stockCode, int version) {
        final Solidarity solidarity = solidarityService.getSolidarityByStockCode(stockCode);

        validateSolidarityLeaderDoesNotExist(solidarity);
        validateUserHasNotBeenApplied(solidarity, version, "이미 주주대표 지원이 완료되었습니다.");
    }

    public void validate(int version, SolidarityLeaderElectionApplyDto applyDto) {
        final Solidarity solidarity = solidarityService.getSolidarityByStockCode(applyDto.getStockCode());
        validateSolidarityLeaderDoesNotExist(solidarity);
        validateApplicationCondition(solidarity, applyDto.getElectionStatus());
        validateUserHasNotBeenApplied(applyDto.getSolidarityLeaderElectionId(), version);

        validateApplicationItemsWhenComplete(applyDto);
        validateWhenSave(applyDto);
    }

    public void validate(SolidarityLeaderElectionApplyDto applyDto, SolidarityLeaderApplicant solidarityLeaderApplicant) {
        final Solidarity solidarity = solidarityService.getSolidarityByStockCode(applyDto.getStockCode());
        validateSolidarityLeaderDoesNotExist(solidarity);
        validateApplicationCondition(solidarity, applyDto.getElectionStatus());

        validateApplicationItemsWhenComplete(applyDto);
        validateWhenSave(applyDto, solidarityLeaderApplicant);
    }

    private void validateWhenSave(SolidarityLeaderElectionApplyDto applyDto) {
        if (applyDto.getApplyStatus() == SAVE) {
            validateApplicationItemsWhenSave(applyDto);
        }
    }

    private void validateWhenSave(SolidarityLeaderElectionApplyDto applyDto, SolidarityLeaderApplicant solidarityLeaderApplicant) {
        if (applyDto.getApplyStatus() == SAVE) {
            validateApplicationItemsWhenSave(applyDto);
            validateCompleteToSave(solidarityLeaderApplicant);
        }
    }

    private void validateApplicationCondition(Solidarity solidarity, SolidarityLeaderElectionStatus originalElectionStatus) {
        if (solidarity.isHasEverHadLeader() || originalElectionStatus == SolidarityLeaderElectionStatus.CANDIDATE_REGISTRATION_PERIOD) {
            return;
        }

        final boolean canLeaderElectionFeatureActive =
            solidarityLeaderElectionFeatureActiveConditionProvider.canActivateLeaderElectionFeature(getMostRecentDailySummary(solidarity));

        if (!canLeaderElectionFeatureActive) {
            throw new BadRequestException("주주대표 충족요건이 달성되지 않아 지원이 불가능합니다.");
        }
    }

    private SolidarityDailySummary getMostRecentDailySummary(Solidarity solidarity) {
        final SolidarityDailySummary mostRecentDailySummary = solidarity.getMostRecentDailySummary();

        if (solidarity.getMostRecentDailySummary() == null) {
            log.error("Solidarity's mostRecentDailySummary is null. stockCode: {}", solidarity.getStockCode());
            throw new BadRequestException("주주대표 지원이 불가합니다. 고객센터에 문의해주세요.");
        }
        return mostRecentDailySummary;
    }

    public void validateCompleteToSave(SolidarityLeaderApplicant applicant) {
        if (applicant.getApplyStatus() == COMPLETE) {
            throw new BadRequestException("이미 제출한 지원서는 임시 저장할 수 없습니다.");
        }
    }

    private void validateApplicationItemsWhenComplete(SolidarityLeaderElectionApplyDto applyDto) {
        if (applyDto.getApplyStatus() == COMPLETE && !isCompleteApplicationItemLengthInRange(applyDto)) {
            throw new BadRequestException(
                "주주대표 지원서 각 항목은 모두 %d자 이상 %d자 이하로 입력해주세요.".formatted(APPLICATION_ITEM_MIN_THRESHOLD, APPLICATION_ITEM_MAX_THRESHOLD)
            );
        }
    }

    private void validateApplicationItemsWhenSave(SolidarityLeaderElectionApplyDto applyDto) {
        if (!isSaveApplicationItemLengthInRange(applyDto)) {
            throw new BadRequestException(
                "주주대표 지원서 각 항목은 모두 %d자 이하로 입력해주세요.".formatted(APPLICATION_ITEM_MAX_THRESHOLD)
            );
        }
    }

    private boolean isCompleteApplicationItemLengthInRange(SolidarityLeaderElectionApplyDto applyDto) {
        return isLengthInRange(applyDto.getReasonsForApply())
            && isLengthInRange(applyDto.getKnowledgeOfCompanyManagement())
            && isLengthInRange(applyDto.getGoals())
            && isLengthInRange(applyDto.getCommentsForStockHolder());
    }

    private boolean isSaveApplicationItemLengthInRange(SolidarityLeaderElectionApplyDto applyDto) {
        return isLengthLessThanMax(applyDto.getReasonsForApply())
            && isLengthLessThanMax(applyDto.getKnowledgeOfCompanyManagement())
            && isLengthLessThanMax(applyDto.getGoals())
            && isLengthLessThanMax(applyDto.getCommentsForStockHolder());
    }

    private boolean isLengthInRange(String applicationItem) {
        return applicationItem.length() >= APPLICATION_ITEM_MIN_THRESHOLD && applicationItem.length() <= APPLICATION_ITEM_MAX_THRESHOLD;
    }

    private boolean isLengthLessThanMax(String applicationItem) {
        return applicationItem.length() <= APPLICATION_ITEM_MAX_THRESHOLD;
    }

    private void validateSolidarityLeaderDoesNotExist(Solidarity solidarity) {
        if (solidarity.getSolidarityLeader() != null) {
            throw new BadRequestException("해당 연대에 이미 주주대표가 존재합니다.");
        }
    }

    private void validateUserHasNotBeenApplied(Solidarity solidarity, int version, String message) {
        final User user = ActUserProvider.getNoneNull();
        if (solidarityLeaderApplicantRepository.existsBySolidarityIdAndUserIdAndVersion(solidarity.getId(), user.getId(), version)) {
            throw new BadRequestException(message);
        }
    }

    private void validateUserHasNotBeenApplied(Long solidarityLeaderElectionId, int version) {
        final User user = ActUserProvider.getNoneNull();

        boolean hasApplyCompleted = solidarityLeaderApplicantRepository.existsBySolidarityLeaderElectionIdAndUserIdAndApplyStatusInAndVersion(
            solidarityLeaderElectionId,
            user.getId(),
            SolidarityLeaderElectionApplyStatus.getApplyStatuses(),
            version
        );

        if (hasApplyCompleted) {
            throw new BadRequestException(ALREADY_CREATE_APPLICANT);
        }
    }
}
