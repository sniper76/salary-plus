package ag.act.validator.solidarity;

import ag.act.dto.solidarity.SolidarityLeaderElectionApplyDto;
import ag.act.entity.User;
import ag.act.exception.BadRequestException;
import ag.act.service.solidarity.election.BlockedSolidarityLeaderApplicantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BlockedSolidarityLeaderElectionApplicantsValidator {
    private final BlockedSolidarityLeaderApplicantService blockedSolidarityLeaderApplicantService;

    public void validate(SolidarityLeaderElectionApplyDto solidarityLeaderElectionApplyDto, User user) {
        final Long userId = user.getId();
        final String stockCode = solidarityLeaderElectionApplyDto.getStockCode();
        blockedSolidarityLeaderApplicantService.findByStockCodeAndUserId(stockCode, userId)
            .ifPresent(blockedSolidarityLeaderApplicant -> {
                throw new BadRequestException("해당 종목에 주주대표 지원이 불가능합니다. 관리자에게 문의해 주세요.");
            });
    }
}
