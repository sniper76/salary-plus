package ag.act.repository;

import ag.act.dto.stock.SimpleStockDto;
import ag.act.entity.Stock;
import ag.act.model.Status;
import ag.act.repository.interfaces.StockSearchResultItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {

    Optional<Stock> findByCode(String code);

    @Query("""
        select s
        from Stock s
        left join fetch s.solidarity
        where s.code in :codes
        """
    )
    List<Stock> findAllByCodeIn(List<String> codes);

    List<Stock> findAllByStandardCodeIn(List<String> standardCodes);

    List<Stock> findTop10ByNameContainingAndStatusIn(String searchKeyword, List<Status> statuses);

    @Query(value = """
        select
            s.code,
            s.name,
            s.total_issued_quantity as totalIssuedQuantity,
            s.representative_phone_number as representativePhoneNumber,
            s.solidarity_id as solidarityId,
            s.status,
            s.is_private as isPrivate,
            s.standard_code as standardCode,
            s.full_name as fullName,
            s.market_type as marketType,
            s.stock_type as stockType,
            s.closing_price as closingPrice,
            s.created_at as createdAt,
            s.updated_at as updatedAt,
            s.deleted_at as deletedAt,
            COALESCE(sds.stake, 0) as stake,
            COALESCE(sds.member_count, 0) as memberCount,
            sds.market_value as marketValue
        from stocks s
             inner join solidarities s2 on s.solidarity_id = s2.id
             left outer join solidarity_daily_summaries sds on s2.most_recent_daily_summary_id = sds.id
             where (s.code in :codes OR :codes is null)
        """, nativeQuery = true
    )
    Page<StockSearchResultItem> findAllByCodeInOrderByStakeDesc(List<String> codes, Pageable pageable);

    @Query(value = """
        select
            s.code,
            s.name,
            s.total_issued_quantity as totalIssuedQuantity,
            s.representative_phone_number as representativePhoneNumber,
            s.solidarity_id as solidarityId,
            s.status,
            s.is_private as isPrivate,
            s.standard_code as standardCode,
            s.full_name as fullName,
            s.market_type as marketType,
            s.stock_type as stockType,
            s.closing_price as closingPrice,
            s.created_at as createdAt,
            s.updated_at as updatedAt,
            s.deleted_at as deletedAt,
            COALESCE(sds.stake, 0) as stake,
            COALESCE(sds.member_count, 0) as memberCount,
            sds.market_value as marketValue
        from stocks s
             inner join solidarities s2 on s.solidarity_id = s2.id
             left outer join solidarity_daily_summaries sds on s2.most_recent_daily_summary_id = sds.id
             where (s.code in :codes OR :codes is null)
             and s.is_private = true
        """, nativeQuery = true
    )
    Page<StockSearchResultItem> findPrivateStocksByCodeIn(List<String> codes, Pageable pageable);

    List<Stock> findTop10ByStatusIn(List<Status> statuses);

    @Query(value = """
        SELECT s.*
        FROM stocks s
        INNER JOIN stock_group_mappings sgm on s.code = sgm.stock_code
        INNER JOIN stock_groups sg on sg.id = sgm.stock_group_id
        WHERE sg.id = :stockGroupId
        ORDER BY s.name
        """, nativeQuery = true)
    List<Stock> findAllByStockGroupId(Long stockGroupId);

    @Query("""
        SELECT new ag.act.dto.stock.SimpleStockDto(s.code, s.name, s.standardCode)
        FROM Stock s
        """
    )
    List<SimpleStockDto> findAllSimpleStocks(Sort name);

    @Query("""
        SELECT new ag.act.dto.stock.SimpleStockDto(s.code, s.name, s.standardCode)
        FROM Stock s
        WHERE s.code = :code
        """
    )
    Optional<SimpleStockDto> findSimpleStockByCode(String code);

    @Query("""
        SELECT new ag.act.dto.stock.SimpleStockDto(s.code, s.name, s.standardCode)
          FROM Stock s
         WHERE s.status = 'ACTIVE'
           AND NOT EXISTS (
                           SELECT 1
                             FROM TestStock ts
                            WHERE ts.code = s.code
                          )
        """
    )
    List<SimpleStockDto> findAllSimpleStocksWithoutTestStocks(Sort name);

    @Query("""
        SELECT s
          FROM Stock s
         WHERE s.status = 'ACTIVE'
           AND NOT EXISTS (
                           SELECT 1
                             FROM TestStock ts
                            WHERE ts.code = s.code
                          )
        """
    )
    List<Stock> findAllStocksWithoutTestStocks();

    @Query(value = """
        SELECT DISTINCT s
        FROM Stock s
        INNER JOIN UserHoldingStock uhs
            ON s.code = uhs.stockCode
            AND uhs.userId = :userId
        LEFT JOIN LatestUserPostsView lupv
            ON s.code = lupv.stock.code
            AND lupv.user.id = :userId
            AND lupv.postsViewType = 'STOCK_HOME'
        LEFT JOIN LatestPostTimestamp lpt
            ON s.code = lpt.stock.code
        WHERE
            (lupv IS NULL AND lpt IS NOT NULL)
            OR (lupv.timestamp < lpt.timestamp)
        """
    )
    List<Stock> findAllWithUnreadPosts(Long userId);

    Optional<Stock> findBySolidarityId(Long solidarityId);
}
