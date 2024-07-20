package ag.act.service.digitaldocument.aop;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.service.digitaldocument.DigitalDocumentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
@RequiredArgsConstructor
public class DigitalDocumentTransactionRequestNewService {
    private final DigitalDocumentUserService digitalDocumentUserService;

    public void deleteDigitalDocumentUser(Long digitalDocumentId) {
        findDigitalDocumentUser(digitalDocumentId)
            .ifPresent(digitalDocumentUser -> digitalDocumentUserService.deleteUserDigitalDocument(digitalDocumentId));
    }

    private Optional<DigitalDocumentUser> findDigitalDocumentUser(Long digitalDocumentId) {
        return digitalDocumentUserService.findByDigitalDocumentIdAndUserId(
            digitalDocumentId,
            ActUserProvider.getNoneNull().getId()
        );
    }
}
