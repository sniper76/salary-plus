package ag.act.repository;

import ag.act.dto.campaign.SimpleCampaignPostDto;
import ag.act.entity.Post;
import ag.act.enums.BoardCategory;
import ag.act.enums.DigitalDocumentType;
import ag.act.model.Status;
import ag.act.module.dashboard.statistics.ICountItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("""
            select p
            from Post p
            where p.board.id in :boardIdList
            and p.status in :statusList
            and p.userId not in :userIdList
            AND p.activeStartDate between :searchStartDateTime AND current_timestamp
            AND (p.activeEndDate is null OR p.activeEndDate >= current_timestamp)
        """)
    Page<Post> findAllByBoardIdInAndStatusInAndUserIdNotIn(
        List<Long> boardIdList, List<Status> statusList, List<Long> userIdList, LocalDateTime searchStartDateTime, Pageable pageable
    );

    @Query(value = """
            SELECT p
            FROM Post p
            WHERE p.board.stockCode = :stockCode
            AND p.board.category IN :boardCategories
            AND p.status IN :statusList
            AND (p.activeStartDate <= :searchEndDateTime
              AND (p.activeEndDate >= :searchStartDateTime
              OR p.activeEndDate IS NULL))
        """)
    Page<Post> findAllByBoardStockCodeAndBoardCategoryInAndStatusInAndIsActiveBetween(
        String stockCode,
        List<BoardCategory> boardCategories,
        List<ag.act.model.Status> statusList,
        LocalDateTime searchStartDateTime,
        LocalDateTime searchEndDateTime,
        Pageable pageable
    );

    Page<Post> findAllByBoardStockCodeAndBoardCategoryInAndStatusInAndUserIdNotIn(
        String stockCode, List<BoardCategory> boardCategories, List<ag.act.model.Status> statusList, List<Long> userIdList, Pageable pageable
    );

    List<Post> findAllByBoardStockCodeAndBoardCategoryAndStatusIn(
        String stockCode, BoardCategory category, List<ag.act.model.Status> statusList, Pageable pageable
    );

    Optional<Post> findByIdAndStatusNotIn(Long postId, List<ag.act.model.Status> statusDeletes);

    List<Post> findAllByBoardId(Long boardId);

    @Query(value = """
            SELECT p
            FROM Post p
            WHERE p.content LIKE %:searchKeyword%
            AND p.board.category IN :categories
            AND p.status IN :statusList
            AND (p.activeStartDate <= :searchEndDateTime
              AND (p.activeEndDate >= :searchStartDateTime
              OR p.activeEndDate IS NULL))
        """)
    Page<Post> findAllByContentContainingAndBoardCategoryInAndStatusInAndIsActiveBetween(
        String searchKeyword,
        List<BoardCategory> categories,
        List<ag.act.model.Status> statusList,
        LocalDateTime searchStartDateTime,
        LocalDateTime searchEndDateTime,
        Pageable pageable
    );


    @Query(value = """
            SELECT p
            FROM Post p
            WHERE p.title LIKE %:searchKeyword%
            AND p.board.category IN :categories
            AND p.status IN :statuses
            AND (p.activeStartDate <= :searchEndDateTime
              AND (p.activeEndDate >= :searchStartDateTime
              OR p.activeEndDate IS NULL))
        """)
    Page<Post> findAllByTitleContainingAndBoardCategoryInAndStatusInAndIsActiveBetween(
        String searchKeyword,
        List<BoardCategory> categories,
        List<Status> statuses,
        LocalDateTime searchStartDateTime,
        LocalDateTime searchEndDateTime,
        Pageable pageable
    );

    @Query(value = """
            SELECT p
            FROM Post p
            WHERE p.status IN :statuses
            AND p.board.category IN :categories
            AND (p.activeStartDate <= :searchEndDateTime
              AND (p.activeEndDate >= :searchStartDateTime
              OR p.activeEndDate IS NULL))
            AND (p.title LIKE %:searchKeyword% OR p.content LIKE %:searchKeyword%)
        """)
    Page<Post> findAllByStatusInAndBoardCategoryInAndTitleContainingOrContentContainingAndActiveDateBetween(
        List<Status> statuses,
        List<BoardCategory> categories,
        String searchKeyword,
        LocalDateTime searchStartDateTime,
        LocalDateTime searchEndDateTime,
        Pageable pageable
    );

    List<Post> findAllBySourcePostId(Long postId);

    @Query("""
        SELECT new ag.act.dto.campaign.SimpleCampaignPostDto(p.id, s.code, s.name)
        FROM Post p
        JOIN FETCH Board b on p.boardId = b.id
        JOIN FETCH Stock s on b.stockCode = s.code
        WHERE p.sourcePostId = :sourcePostId
        """)
    List<SimpleCampaignPostDto> findAllSimpleCampaignPostDtosBySourcePostId(Long sourcePostId);

    @Query(value = """
        select s.code as title, coalesce(count(p.id), 0) as "value"
        from stocks s
        inner join boards b on s.code = b.stock_code
        inner join posts p on b.id = p.board_id and p.status = 'ACTIVE' and p.created_at between :startDateTime and :endDateTime
         group by s.code
        """, nativeQuery = true)
    List<ICountItem> findPostCountByStock(LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query(value = """
        select s.code as title, coalesce(sum(p.comment_count), 0) as "value"
        from stocks s
        inner join boards b on s.code = b.stock_code
        inner join posts p on b.id = p.board_id and p.status = 'ACTIVE' and p.created_at between :startDateTime and :endDateTime
         group by s.code
        """, nativeQuery = true)
    List<ICountItem> findCommentCountByStock(LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query(value = """
        select s.code as title, coalesce(sum(p.like_count), 0) as "value"
        from stocks s
        inner join boards b on s.code = b.stock_code
        inner join posts p on b.id = p.board_id and p.status = 'ACTIVE' and p.created_at between :startDateTime and :endDateTime
         group by s.code
        """, nativeQuery = true)
    List<ICountItem> findLikedCountByStock(LocalDateTime startDateTime, LocalDateTime endDateTime);

    Page<Post> findAllByBoardCategoryInAndStatusInAndUserIdNotInAndLikeCountGreaterThanEqual(
        List<BoardCategory> categories,
        List<ag.act.model.Status> statusList,
        List<Long> userIdList,
        Long likeCount,
        Pageable pageable
    );

    int countByBoardCategoryInAndStatusInAndLikeCountGreaterThanEqual(
        List<BoardCategory> categories,
        List<ag.act.model.Status> statusList,
        Long likeCount
    );

    Page<Post> findAllByBoardCategoryInAndStatusInAndUserIdNotInAndLikeCountGreaterThanEqualAndIsExclusiveToHolders(
        List<BoardCategory> categories,
        List<Status> statuses,
        List<Long> blockedUserIds,
        Long likeCount,
        boolean isExclusiveToHolders,
        Pageable pageable
    );

    @Query(value = """
        SELECT p
        FROM Post p
        WHERE p.board.category IN :categories
        AND p.status IN :statusList
        AND p.likeCount >= :likeCount
        AND p.isExclusiveToHolders = true
        AND p.board.stockCode IN :stockCodeList
        AND p.userId NOT IN :blockedUserIdList
        """)
    Page<Post> getBestPostsForOnlyExclusiveToHolders(
        @Param("categories") List<BoardCategory> categories,
        @Param("statusList") List<Status> statusList,
        @Param("stockCodeList") List<String> stockCodeList,
        @Param("blockedUserIdList") List<Long> blockedUserIdList,
        @Param("likeCount") Long likeCount,
        Pageable pageable
    );

    @Query(value = """
            select
                p.*,
                p.created_at as createdAt,
                p.updated_at as updatedAt,
                case when dd.target_start_date <= current_timestamp and current_timestamp <= dd.target_end_date  then 0
                     when dd.target_start_date > current_timestamp then 1
                     else 2 end as sortIndex
            from posts p
            inner join digital_documents dd on p.id = dd.post_id
            inner join user_holding_stock_on_reference_dates uhsord
                     on dd.stock_code = uhsord.stock_code
                    and dd.stock_reference_date = uhsord.reference_date
                    and uhsord.user_id = :userId
            where dd.type = :type
            and p.status = 'ACTIVE'
        """, nativeQuery = true)
    Page<Post> findAllByTypeAndUserId(
        @Param("type") String type,
        @Param("userId") Long userId,
        Pageable pageable
    );

    Page<Post> findAllByDigitalDocumentTypeAndDigitalDocumentAcceptUserId(
        @Param("type") DigitalDocumentType type,
        @Param("acceptUserId") Long acceptUserId,
        Pageable pageable
    );

    Page<Post> findAllByDigitalDocumentTypeAndDigitalDocumentAcceptUserIdAndTitleContaining(
        @Param("type") DigitalDocumentType type,
        @Param("acceptUserId") Long acceptUserId,
        @Param("searchKeyword") String searchKeyword,
        Pageable pageable
    );

    long countByUserIdAndStatus(Long userId, Status status);

    Optional<Post> findFirstByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(Long userId, LocalDateTime localDateTime);
}
