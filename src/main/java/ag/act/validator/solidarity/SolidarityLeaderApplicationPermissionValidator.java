package ag.act.validator.solidarity;

import ag.act.configuration.security.ActUserProvider;
import ag.act.dto.SolidarityLeaderApplicationDto;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.User;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class SolidarityLeaderApplicationPermissionValidator {

    public void validateViewPermission(SolidarityLeaderApplicationDto applicationDto) {
        validateOwnerByApplyStatus(applicationDto.getUserId(), applicationDto.getApplyStatus());
    }

    public void validateEditPermission(SolidarityLeaderApplicant applicant) {
        validateOwnerByApplyStatus(applicant.getUserId());
    }

    private void validateOwnerByApplyStatus(Long applicantUserId, SolidarityLeaderElectionApplyStatus applyStatus) {
        if (SolidarityLeaderElectionApplyStatus.isSave(applyStatus) && !isAuthor(applicantUserId)) {
            throw new BadRequestException("임시 저장된 지원서는 작성자만 확인할 수 있습니다.");
        }
    }

    private void validateOwnerByApplyStatus(Long applicantUserId) {
        if (!isAuthor(applicantUserId)) {
            throw new BadRequestException("본인이 작성한 지원서만 수정할 수 있습니다.");
        }
    }

    private boolean isAuthor(Long applicantUserId) {
        final User user = ActUserProvider.getNoneNull();
        return user.getId().equals(applicantUserId);
    }

    private boolean isAuthor(User user, Long applicantUserId) {
        return user.getId().equals(applicantUserId);
    }

    public void validateWithdraw(SolidarityLeaderApplicant applicant, String message) {
        User user = ActUserProvider.getNoneNull();

        if (user.isAdmin()) {
            return;
        }

        if (!isAuthor(user, applicant.getUserId())) {
            throw new BadRequestException(message);
        }
    }
}
