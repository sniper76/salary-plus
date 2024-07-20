package ag.act.service.user;

import ag.act.entity.User;
import ag.act.exception.BadRequestException;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserWithdrawalRequestValidator {
    private final SolidarityLeaderService solidarityLeaderService;
    private final DigitalDocumentService digitalDocumentService;
    private final SolidarityLeaderApplicantService solidarityLeaderApplicantService;

    public void validate(User user) {
        validateIfSolidarityLeader(user);
        validateIfAcceptorUser(user);
        validateIfAppliedForActiveSolidarityLeaderElection(user);
    }

    private void validateIfAppliedForActiveSolidarityLeaderElection(User user) {
        if (isUserAppliedForActiveSolidarityLeaderElection(user)) {
            throw new BadRequestException("주주대표 선출 진행 중에는 회원탈퇴를 할 수 없습니다.");
        }
    }

    private void validateIfAcceptorUser(User user) {
        if (isAcceptorUser(user)) {
            throw new BadRequestException("수임인으로 진행중인 전자문서가 존재하여 탈퇴할 수 없습니다. 관리자에게 문의해주세요.");
        }
    }

    private void validateIfSolidarityLeader(User user) {
        if (isSolidarityLeader(user)) {
            throw new BadRequestException("주주대표는 탈퇴할 수 없습니다. 관리자에게 문의해주세요.");
        }
    }

    private boolean isAcceptorUser(User user) {
        return digitalDocumentService.existsProcessingByAcceptor(user.getId());
    }

    private boolean isSolidarityLeader(User user) {
        return solidarityLeaderService.isLeader(user.getId());
    }

    private boolean isUserAppliedForActiveSolidarityLeaderElection(User user) {
        return solidarityLeaderApplicantService
            .isUserAppliedForActiveSolidarityLeaderElection(user.getId());
    }
}
