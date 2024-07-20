package ag.act.repository;

import ag.act.entity.digitaldocument.DigitalDocumentDownload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DigitalDocumentDownloadRepository extends JpaRepository<DigitalDocumentDownload, Long> {
    List<DigitalDocumentDownload> findAllByDigitalDocumentId(Long digitalDocumentId);

    Optional<DigitalDocumentDownload> findFirstByZipFileKey(String zipFileKey);
}
