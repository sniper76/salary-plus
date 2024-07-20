package ag.act.validator.post;

import ag.act.exception.BadRequestException;
import ag.act.model.Status;
import ag.act.service.user.UserRoleService;
import ag.act.util.StatusUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminStockBoardGroupPostCommentValidator {
    private final UserRoleService userRoleService;

    public void validateAdminRole(Long userId) {
        if (!userRoleService.isAdmin(userId)) {
            throw new BadRequestException("관리자 권한으로 작성된 댓글/답글만 수정 가능합니다.");
        }
    }

    public void validateUpdateStatusForAdmin(Status status) {
        if (!StatusUtil.getPossibleUpdateStatusesForAdmin().contains(status)) {
            throw new BadRequestException("삭제/삭제취소 상태만 수정 가능합니다.");
        }
    }
}
