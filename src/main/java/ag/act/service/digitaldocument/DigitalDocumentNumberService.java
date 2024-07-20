package ag.act.service.digitaldocument;

import ag.act.entity.digitaldocument.DigitalDocumentNumber;
import ag.act.repository.DigitalDocumentNumberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class DigitalDocumentNumberService {
    private static final int DIGITAL_DOCUMENT_USER_NUMBER_MAX_RETRY_COUNT = 10;
    private final DigitalDocumentNumberRepository digitalDocumentNumberRepository;

    @Retryable(
        retryFor = PessimisticLockingFailureException.class,
        maxAttempts = DIGITAL_DOCUMENT_USER_NUMBER_MAX_RETRY_COUNT,
        backoff = @Backoff(delay = 1000)
    )
    public long increaseAndGetLastIssuedNumber(Long digitalDocumentId) {
        final DigitalDocumentNumber digitalDocumentNumber = findByDigitalDocumentId(digitalDocumentId);
        long lastIssuedNumber = digitalDocumentNumber.getLastIssuedNumber() + 1;

        digitalDocumentNumber.setDigitalDocumentId(digitalDocumentId);
        digitalDocumentNumber.setLastIssuedNumber(lastIssuedNumber);

        save(digitalDocumentNumber);

        return lastIssuedNumber;
    }

    public DigitalDocumentNumber findByDigitalDocumentId(Long digitalDocumentId) {
        return digitalDocumentNumberRepository.findByDigitalDocumentId(digitalDocumentId)
            .orElse(new DigitalDocumentNumber(0L));
    }

    public DigitalDocumentNumber save(DigitalDocumentNumber digitalDocumentNumber) {
        return digitalDocumentNumberRepository.save(digitalDocumentNumber);
    }
}