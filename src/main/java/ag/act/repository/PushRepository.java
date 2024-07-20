package ag.act.repository;

import ag.act.dto.admin.AutomatedAuthorPushDto;
import ag.act.entity.Push;
import ag.act.enums.AutomatedPushContentType;
import ag.act.enums.AutomatedPushCriteria;
import ag.act.enums.push.PushSendStatus;
import ag.act.enums.push.PushTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("checkstyle:LineLength")
@Repository
public interface PushRepository extends JpaRepository<Push, Long> {
    List<Push> findAllByPostId(Long postId);

    List<Push> findAllByPostIdAndSendStatus(Long postId, PushSendStatus sendStatus);

    Page<Push> findAllByPushTargetTypeNot(PushTargetType pushTargetType, Pageable pageable);

    Page<Push> findAllByPushTargetTypeNotAndStockNameContaining(PushTargetType pushTargetType, String stockName, Pageable pageable);

    Page<Push> findAllByPushTargetTypeNotAndStockGroupNameContaining(PushTargetType pushTargetType, String stockGroupName, Pageable pageable);

    Page<Push> findAllByPushTargetTypeNotAndContentContaining(PushTargetType pushTargetType, String content, Pageable pageable);

    Page<Push> findAllByPushTargetTypeNotAndTitleContaining(PushTargetType pushTargetType, String title, Pageable pageable);

    List<Push> findAllBySendStatusAndTargetDatetimeLessThanEqualOrderByTargetDatetimeAsc(PushSendStatus sendStatus, LocalDateTime targetDatetime);

    @Query("""
        select p
        from Push p
        inner join AutomatedAuthorPush aap on p.id = aap.pushId
        where aap.contentId = :contentId
        and aap.contentType = :contentType
        and aap.criteria = :criteria
        and p.targetDatetime >= :targetDateTime
        order by p.targetDatetime desc
        """)
    List<Push> findAllThatHaveAutomatedAuthorPush(
        @Param("contentId") Long contentId,
        @Param("contentType") AutomatedPushContentType contentType,
        @Param("criteria") AutomatedPushCriteria criteria,
        @Param("targetDateTime") LocalDateTime targetDateTime
    );

    @Query("""
        select new ag.act.dto.admin.AutomatedAuthorPushDto(pu, u)
        from Push pu
        inner join AutomatedAuthorPush aap on pu.id = aap.pushId
        inner join Post po on aap.contentId = po.id
        inner join User u on po.userId = u.id
        where pu.pushTargetType = :pushTargetType
        """)
    Page<AutomatedAuthorPushDto> findAllByPushTargetType(
        @Param("pushTargetType") PushTargetType pushTargetType, Pageable pageable
    );

    @Query("""
        select new ag.act.dto.admin.AutomatedAuthorPushDto(pu, u)
        from Push pu
        inner join AutomatedAuthorPush aap on pu.id = aap.pushId
        inner join Post po on aap.contentId = po.id
        inner join User u on po.userId = u.id
        where pu.pushTargetType = :pushTargetType
        and u.name like :name
        """)
    Page<AutomatedAuthorPushDto> findAllByPushTargetTypeAndNameContaining(
        @Param("pushTargetType") PushTargetType pushTargetType, @Param("name") String name, Pageable pageable
    );

    @Query("""
        select new ag.act.dto.admin.AutomatedAuthorPushDto(pu, u)
        from Push pu
        inner join AutomatedAuthorPush aap on pu.id = aap.pushId
        inner join Post po on aap.contentId = po.id
        inner join User u on po.userId = u.id
        where pu.pushTargetType = :pushTargetType
        and u.nickname like :nickname
        """)
    Page<AutomatedAuthorPushDto> findAllByPushTargetTypeAndNicknameContaining(
        @Param("pushTargetType") PushTargetType pushTargetType, @Param("nickname") String nickname, Pageable pageable
    );

    @Query("""
        select new ag.act.dto.admin.AutomatedAuthorPushDto(pu, u)
        from Push pu
        inner join AutomatedAuthorPush aap on pu.id = aap.pushId
        inner join Post po on aap.contentId = po.id
        inner join User u on po.userId = u.id
        where pu.pushTargetType = :pushTargetType
        and pu.content like :keyword
        """)
    Page<AutomatedAuthorPushDto> findAllByPushTargetTypeAndContentContaining(
        @Param("pushTargetType") PushTargetType pushTargetType, @Param("keyword") String keyword, Pageable pageable
    );

    @Query("""
        select new ag.act.dto.admin.AutomatedAuthorPushDto(pu, u)
        from Push pu
        inner join AutomatedAuthorPush aap on pu.id = aap.pushId
        inner join Post po on aap.contentId = po.id
        inner join User u on po.userId = u.id
        where pu.pushTargetType = :pushTargetType
        and pu.title like :keyword
        """)
    Page<AutomatedAuthorPushDto> findAllByPushTargetTypeAndTitleContaining(
        @Param("pushTargetType") PushTargetType pushTargetType, @Param("keyword") String keyword, Pageable pageable
    );

    void deleteAllByPostIdAndSendStatus(Long postId, PushSendStatus sendStatus);
}
