package ag.act.service.digitaldocument;

import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.repository.DigitalDocumentUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DigitalDocumentAnswerService {
    private final DigitalDocumentUserRepository digitalDocumentUserRepository;

    public DigitalDocumentAnswerService(DigitalDocumentUserRepository digitalDocumentUserRepository) {
        this.digitalDocumentUserRepository = digitalDocumentUserRepository;
    }

    public DigitalDocumentAnswerStatus getDigitalDocumentAnswerStatus(User user, DigitalDocument digitalDocument) {
        return digitalDocumentUserRepository.findByDigitalDocumentIdAndUserId(
                digitalDocument.getId(), user.getId()
            )
            .orElseGet(DigitalDocumentUser::new)
            .getDigitalDocumentAnswerStatus();
    }
}
