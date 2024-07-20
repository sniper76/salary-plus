package ag.act.repository;

import ag.act.entity.digitaldocument.DigitalDocumentNumber;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DigitalDocumentNumberRepository extends JpaRepository<DigitalDocumentNumber, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "10000")})
    Optional<DigitalDocumentNumber> findByDigitalDocumentId(Long digitalDocumentId);
}
