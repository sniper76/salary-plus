package ag.act.validator.user;

import ag.act.entity.NicknameHistory;
import ag.act.entity.User;
import ag.act.exception.BadRequestException;
import ag.act.service.StopWordService;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import ag.act.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class NicknameValidator {
    private final int nicknameChangeLimitDays;
    private final StopWordService stopWordService;
    private final SolidarityLeaderApplicantService solidarityLeaderApplicantService;

    public NicknameValidator(
        @Value("${act.user.nickname.change-limit-days:90}") int nicknameChangeLimitDays,
        StopWordService stopWordService,
        @Lazy SolidarityLeaderApplicantService solidarityLeaderApplicantService
    ) {
        this.nicknameChangeLimitDays = nicknameChangeLimitDays;
        this.stopWordService = stopWordService;
        this.solidarityLeaderApplicantService = solidarityLeaderApplicantService;
    }

    public void validateNickname(User user) {
        if (stopWordService.containsStopWord(user.getNickname())) {
            throw new BadRequestException("닉네임에 사용할 수 없는 단어입니다.");
        }
    }

    public void validateNicknameWithin90Days(User user) {
        final NicknameHistory nicknameHistory = getCurrentNicknameHistory(user);

        if (nicknameHistory == null) {
            return;
        }

        if (nicknameHistory.getIsFirst()) {
            return;
        }

        if (DateTimeUtil.isBeforeInDays(nicknameHistory.getCreatedAt(), nicknameChangeLimitDays)) {
            return;
        }

        throw new BadRequestException("닉네임은 %s일에 한번만 변경할 수 있습니다.".formatted(nicknameChangeLimitDays));
    }

    public void validateIfUserAppliedForSolidarityLeaderElection(User user) {
        if (!isUserAppliedForActiveSolidarityLeaderElection(user)) {
            return;
        }

        throw new BadRequestException("주주대표 지원자는 대표 선출 기간 동안 주주들의 혼선을 방지하기 위하여 닉네임 변경이 불가합니다.");
    }

    private boolean isUserAppliedForActiveSolidarityLeaderElection(User user) {
        return solidarityLeaderApplicantService
            .isUserAppliedForActiveSolidarityLeaderElection(user.getId());
    }

    private NicknameHistory getCurrentNicknameHistory(User user) {
        NicknameHistory nicknameHistory = user.getNicknameHistory();

        if (nicknameHistory == null || !nicknameHistory.isByAdmin()) {
            return nicknameHistory;
        }

        return user.getNicknameHistories()
            .stream()
            .sorted(Comparator.comparing(NicknameHistory::getId).reversed())
            .filter(it -> !it.isByAdmin()).findFirst().orElse(null);
    }
}
