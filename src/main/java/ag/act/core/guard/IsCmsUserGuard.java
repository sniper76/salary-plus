package ag.act.core.guard;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.NotFoundException;
import ag.act.service.digitaldocument.DigitalDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class IsCmsUserGuard implements ActGuard {
    private final DigitalDocumentService digitalDocumentService;

    public void canActivate(Map<String, Object> parameterMap) throws ActRuntimeException {
        final User user = ActUserProvider.getNoneNull();
        if (user.isAdmin()) {
            return;
        }
        final Long digitalDocumentId = (Long) parameterMap.get("digitalDocumentId");
        final DigitalDocument digitalDocument = digitalDocumentService.getDigitalDocument(digitalDocumentId);
        final Long acceptUserId = digitalDocument.getAcceptUserId();
        if (!Objects.equals(user.getId(), acceptUserId)) {
            throw new NotFoundException("전자문서 정보가 없습니다. 수임인명 : %s".formatted(user.getName()));
        }
    }
}
