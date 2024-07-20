package ag.act.repository;

import ag.act.entity.digitaldocument.HolderListReadAndCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HolderListReadAndCopyRepository extends JpaRepository<HolderListReadAndCopy, Long> {

    List<HolderListReadAndCopy> findAllByDigitalDocumentId(Long digitalDocumentId);

}
