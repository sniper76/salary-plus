package ag.act.repository;

import ag.act.dto.DigitalDocumentUserAnswerDto;
import ag.act.entity.digitaldocument.DigitalDocumentItemUserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DigitalDocumentItemUserAnswerRepository extends JpaRepository<DigitalDocumentItemUserAnswer, Long> {

    @Query("""
        select new ag.act.dto.DigitalDocumentUserAnswerDto(d.id, di.id, diua.id, diua.userId, di.defaultSelectValue, diua.answerSelectValue)
        from DigitalDocument d
        inner join DigitalDocumentItem di on d.id = di.digitalDocumentId
        inner join DigitalDocumentItemUserAnswer diua on di.id = diua.digitalDocumentItemId
        where d.id = :id and diua.userId = :userId
        """
    )
    List<DigitalDocumentUserAnswerDto> findUserAnswerList(@Param("id") Long digitalDocumentId, @Param("userId") Long userId);

    List<DigitalDocumentItemUserAnswer> findAllByUserId(Long userId);

    @Query("""
        select diua
        from DigitalDocumentItem di
        inner join DigitalDocumentItemUserAnswer diua on di.id = diua.digitalDocumentItemId
        where di.digitalDocumentId = :id
          and diua.userId IN :userIds
        order by diua.userId, diua.id
        """
    )
    List<DigitalDocumentItemUserAnswer> findAllByDigitalDocumentIdAndUserIdInOrderByUserIdAscOrderByIdAsc(
        @Param("id") Long digitalDocumentId, @Param("userIds") List<Long> userIds);
}
