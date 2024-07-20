package ag.act.repository;

import ag.act.entity.Campaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    @Query("""
        select dd.id
        from DigitalDocument dd
        join fetch Post p on dd.postId = p.id
        where p.id = :postId
        or p.sourcePostId = :postId
        """
    )
    List<Long> findAllDigitalDocumentIdsBySourcePostId(Long postId);

    Page<Campaign> findAll(Pageable pageable);

    @Query(value = """
            select c.id,
                c.title,
                c.source_post_id,
                c.source_stock_group_id,
                c.status,
                c.created_at as createdAt,
                c.updated_at as updatedAt,
                c.deleted_at as deletedAt,
                c.created_at,
                c.updated_at,
                c.deleted_at
            from campaigns c
            inner join posts p on c.source_post_id = p.id
            inner join boards b on p.board_id = b.id
            where (b.category = :boardCategory or :boardCategory is null)
            and c.status <> 'DELETED'
        """, nativeQuery = true)
    Page<Campaign> findAllByBoardCategory(
        @Param("boardCategory") String boardCategory,
        PageRequest pageRequest
    );

    @Query(value = """
            select c.id,
                c.title,
                c.source_post_id,
                c.source_stock_group_id,
                c.status,
                c.created_at as createdAt,
                c.updated_at as updatedAt,
                c.deleted_at as deletedAt,
                c.created_at,
                c.updated_at,
                c.deleted_at
            from campaigns c
            inner join posts p on c.source_post_id = p.id
            inner join boards b on p.board_id = b.id
            inner join stock_groups sg on c.source_stock_group_id = sg.id
            where (b.category = :boardCategory or :boardCategory is null)
            and c.status <> 'DELETED'
            and sg.name like %:searchKeyword%
        """, nativeQuery = true)
    Page<Campaign> findAllByBoardCategoryAndStockGroupNameContaining(
        @Param("boardCategory") String boardCategory,
        @Param("searchKeyword") String searchKeyword,
        PageRequest pageRequest
    );

    @Query(value = """
            select c.id,
                c.title,
                c.source_post_id,
                c.source_stock_group_id,
                c.status,
                c.created_at as createdAt,
                c.updated_at as updatedAt,
                c.deleted_at as deletedAt,
                c.created_at,
                c.updated_at,
                c.deleted_at
            from campaigns c
            inner join posts p on c.source_post_id = p.id
            inner join boards b on p.board_id = b.id
            where (b.category = :boardCategory or :boardCategory is null)
            and c.status <> 'DELETED'
            and c.title like %:searchKeyword%
        """, nativeQuery = true)
    Page<Campaign> findAllByBoardCategoryAndTitleContaining(
        @Param("boardCategory") String boardCategory,
        @Param("searchKeyword") String searchKeyword,
        Pageable pageable
    );

    @Query("""
            SELECT c.sourcePostId
            FROM Campaign c
            WHERE c.id = :campaignId
        """)
    Optional<Long> findSorucePostIdByCampaignId(Long campaignId);
}
