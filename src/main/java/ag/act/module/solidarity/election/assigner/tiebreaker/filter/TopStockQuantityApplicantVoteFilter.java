package ag.act.module.solidarity.election.assigner.tiebreaker.filter;

import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.exception.NotFoundException;
import ag.act.module.solidarity.election.ApplicantVote;
import ag.act.module.solidarity.election.assigner.tiebreaker.dto.TieBreakerDto;
import ag.act.module.solidarity.election.assigner.tiebreaker.dto.UserHoldingStockQuantityTieBreakerDto;
import ag.act.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Component
public class TopStockQuantityApplicantVoteFilter implements TieBreakerFilter {
    private final UserService userService;
    private final TieBreakerBaseFilter tieBreakerBaseFilter;

    public List<ApplicantVote> filter(List<ApplicantVote> tieApplicantVoteList) {
        return tieBreakerBaseFilter.filter(tieApplicantVoteList, this::toUserHoldingStockQuantityTieBreakerDto);
    }

    private TieBreakerDto<Long> toUserHoldingStockQuantityTieBreakerDto(ApplicantVote applicantVote) {
        return new UserHoldingStockQuantityTieBreakerDto(applicantVote, getUserHoldingStockQuantity(applicantVote));
    }

    private long getUserHoldingStockQuantity(ApplicantVote applicantVote) {
        return getUserByApplicantId(applicantVote.applicantId())
            .getUserHoldingStocks()
            .stream()
            .filter(userHoldingStock -> userHoldingStock.getStockCode().equals(applicantVote.stockCode()))
            .findFirst()
            .map(UserHoldingStock::getQuantity)
            .orElse(0L);
    }

    private User getUserByApplicantId(Long applicantId) {
        return userService.findUserByApplicantId(applicantId)
            .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));
    }
}
