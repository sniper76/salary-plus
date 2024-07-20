package ag.act.repository;

import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.repository.interfaces.DigitalDocumentUserSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DigitalDocumentUserRepository extends JpaRepository<DigitalDocumentUser, Long> {
    Optional<DigitalDocumentUser> findByDigitalDocumentIdAndUserId(Long digitalDocumentId, Long userId);

    void deleteAllByUserId(Long userId);

    List<DigitalDocumentUser> findAllByUserId(Long userId);

    List<DigitalDocumentUser> findAllByDigitalDocumentId(Long digitalDocumentId);

    List<DigitalDocumentUser> findAllByDigitalDocumentAnswerStatusIn(List<DigitalDocumentAnswerStatus> statuses);

    List<DigitalDocumentUser> findAllByDigitalDocumentIdAndDigitalDocumentAnswerStatusIn(
        Long digitalDocumentId,
        List<DigitalDocumentAnswerStatus> statuses
    );

    Page<DigitalDocumentUser> findAllByDigitalDocumentIdAndDigitalDocumentAnswerStatusIn(
        Long digitalDocumentId,
        List<DigitalDocumentAnswerStatus> statuses,
        Pageable pageable
    );

    Page<DigitalDocumentUser> findAllByDigitalDocumentIdInAndDigitalDocumentAnswerStatusIn(
        List<Long> digitalDocumentId,
        List<DigitalDocumentAnswerStatus> statuses,
        Pageable pageable
    );

    Page<DigitalDocumentUser> findAllByDigitalDocumentIdAndDigitalDocumentAnswerStatusInAndNameContaining(
        Long digitalDocumentId,
        List<DigitalDocumentAnswerStatus> statuses,
        String name,
        Pageable pageable
    );

    @Query("""
        select COALESCE(sum(ddu.stockCount), 0) as sumOfStockCount, count(ddu.id) as countOfUser
        from DigitalDocumentUser ddu
        where ddu.digitalDocumentId = :digitalDocumentId
        and ddu.digitalDocumentAnswerStatus = 'COMPLETE'
        """
    )
    DigitalDocumentUserSummary findDigitalDocumentUserSummary(Long digitalDocumentId);

}
