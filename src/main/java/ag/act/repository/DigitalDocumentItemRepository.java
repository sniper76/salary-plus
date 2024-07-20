package ag.act.repository;

import ag.act.dto.DigitalDocumentItemWithAnswerDto;
import ag.act.entity.digitaldocument.DigitalDocumentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DigitalDocumentItemRepository extends JpaRepository<DigitalDocumentItem, Long> {
    int countByIdInAndIsLastItem(List<Long> itemIds, boolean isLastItem);

    @Query("""
        select new ag.act.dto.DigitalDocumentItemWithAnswerDto(
            ddi.title, ddi.content, diua.answerSelectValue, ddi.isLastItem
        )
        from DigitalDocumentItem ddi
        left join DigitalDocumentItemUserAnswer diua on ddi.id = diua.digitalDocumentItemId and diua.userId = :userId
        where ddi.digitalDocumentId = :digitalDocumentId
        order by ddi.createdAt asc
        """
    )
    List<DigitalDocumentItemWithAnswerDto> findEveryItemsWithAnswerByUser(
        @Param("digitalDocumentId") Long digitalDocumentId,
        @Param("userId") Long userId
    );

    List<DigitalDocumentItem> findDigitalDocumentItemsByDigitalDocumentId(Long digitalDocumentId);

    int countByDigitalDocumentIdAndIsLastItem(Long digitalDocumentId, boolean isLastItem);

    List<DigitalDocumentItem> findAllByDigitalDocumentIdAndIsLastItemTrueOrderByIdAsc(Long digitalDocumentId);
}
