package ag.act.core.guard;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.BadRequestException;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.user.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class IsAdminOrAcceptorUserGuard implements ActGuard {

    private final DigitalDocumentService digitalDocumentService;
    private final UserRoleService userRoleService;

    @Override
    public void canActivate(Map<String, Object> parameterMap) throws ActRuntimeException {
        final User user = ActUserProvider.getNoneNull();

        if (isAdmin(user)) {
            return;
        }

        final Long postId = (Long) parameterMap.get("postId");
        Optional<DigitalDocument> digitalDocument = digitalDocumentService.findDigitalDocumentByPostId(postId);
        if (!isAcceptorUserOfPost(digitalDocument, user)) {
            throw new BadRequestException("권한이 없습니다.");
        }
    }

    private boolean isAcceptorUserOfPost(Optional<DigitalDocument> digitalDocument, User user) {
        return digitalDocument.map(document -> document.getAcceptUserId().equals(user.getId()))
            .orElse(false);
    }

    private boolean isAdmin(User user) {
        return user.isAdmin() || userRoleService.isAdmin(user.getId());
    }
}
