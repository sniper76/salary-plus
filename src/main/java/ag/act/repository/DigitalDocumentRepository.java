package ag.act.repository;

import ag.act.dto.DigitalDocumentMatchingUserDto;
import ag.act.dto.digitaldocument.DigitalDocumentProgressPeriodDto;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.DigitalDocumentType;
import ag.act.model.Status;
import ag.act.repository.interfaces.JoinCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DigitalDocumentRepository extends JpaRepository<DigitalDocument, Long> {

    @Query("""
            SELECT new ag.act.dto.DigitalDocumentMatchingUserDto(dd, ddu) FROM DigitalDocument dd
            LEFT JOIN dd.post as p
            LEFT JOIN dd.digitalDocumentUserList ddu ON ddu.userId = :userId
            WHERE dd.stockCode in :stockCodes
            AND dd.targetStartDate <= current_timestamp
            AND dd.targetEndDate >= current_timestamp
            AND p.status = 'ACTIVE'
        """)
    List<DigitalDocumentMatchingUserDto> findAllInProgressDigitalDocumentWithMatchingUser(
        Long userId, List<String> stockCodes
    );

    @Query("""
            SELECT dd
            FROM DigitalDocument dd
            JOIN FETCH dd.post as p
            INNER JOIN StockReferenceDate srd
                on dd.stockReferenceDate = srd.referenceDate and dd.stockCode = srd.stockCode and srd.id = :stockReferenceDateId
            WHERE dd.stockCode = :stockCode
            AND p.status = 'ACTIVE'
            AND dd.type = 'DIGITAL_PROXY'
        """)
    List<DigitalDocument> findAllDigitalDocumentByStockReferenceDateIdAndStockCode(
        Long stockReferenceDateId, String stockCode
    );

    @Query(value = """
        select sum(dd.join_stock_sum) as stockQuantity,
            sum(dd.join_user_count) as joinCnt
        from digital_documents dd
        inner join posts p on dd.post_id = p.id
        where (p.id = :postId or p.source_post_id = :postId)
        """, nativeQuery = true)
    Optional<JoinCount> findDigitalDocumentCountByPostId(@Param("postId") Long postId);

    Optional<DigitalDocument> findByPostId(Long postId);

    Optional<DigitalDocument> findByStockCodeAndStockReferenceDate(String stockCode, LocalDate stockReferenceDate);

    List<DigitalDocument> findAllByTypeAndStatusAndTargetStartDateIsLessThanAndTargetEndDateIsGreaterThanEqual(
        DigitalDocumentType type, Status status, LocalDateTime startDate, LocalDateTime endDate
    );

    @Query("""
        select d
        from DigitalDocument d
        join fetch d.post p
        where p.status = :status
        and d.targetEndDate <= :endDate
        """)
    List<DigitalDocument> findAllByStatusAndTargetEndDateIsLessThanEqual(
        @Param("status") Status status, @Param("endDate") LocalDateTime endDate
    );

    boolean existsByStockCodeAndAcceptUserIdAndTargetEndDateGreaterThanEqual(String stockCode, Long userId, LocalDateTime targetEndDate);

    boolean existsByAcceptUserIdAndTargetEndDateGreaterThanEqual(Long userId, LocalDateTime targetEndDate);

    @Query("""
            SELECT new ag.act.dto.digitaldocument.DigitalDocumentProgressPeriodDto(d.targetStartDate, d.targetEndDate)
            FROM DigitalDocument d
            WHERE d.postId = :postId
        """)
    Optional<DigitalDocumentProgressPeriodDto> findDigitalDocumentProgressPeriodByDigitalDocumentId(Long postId);

    @Query("""
            SELECT d.postId
            FROM DigitalDocument d
            WHERE d.id = :digitalDocumentId
        """)
    Optional<Long> findDigitalDocumentPostIdByDigitalDocumentId(Long digitalDocumentId);
}
