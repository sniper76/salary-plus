package ag.act.repository;

import ag.act.dto.MySolidarityDto;
import ag.act.dto.admin.UserDummyStockDto;
import ag.act.entity.UserHoldingStock;
import ag.act.module.dashboard.statistics.ICountItem;
import ag.act.repository.interfaces.SimpleStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserHoldingStockRepository extends JpaRepository<UserHoldingStock, Long> {

    Optional<UserHoldingStock> findFirstByUserIdAndStockCode(Long userId, String stockCode);

    int countByStockCodeAndStatus(String stockCode, ag.act.model.Status status);

    @Query("SELECT SUM(u.quantity) FROM UserHoldingStock u WHERE u.stockCode = :stockCode AND u.status = 'ACTIVE'")
    Optional<Long> sumQuantityOfActiveUserHoldingStockByStockCode(String stockCode);

    void deleteAllByUserId(Long userId);

    void deleteAllByUserIdAndStockCodeAndStatusIsNull(Long userId, String stockCode);

    @Query("""
        SELECT new ag.act.dto.MySolidarityDto(uhs, st, s, mrd, sr) 
        FROM UserHoldingStock uhs
          JOIN FETCH uhs.stock st
          LEFT JOIN FETCH st.solidarity s
          LEFT JOIN FETCH s.solidarityLeader sl
          LEFT JOIN FETCH s.mostRecentDailySummary mrd
          LEFT JOIN StockRanking sr ON st.code = sr.stockCode AND sr.date = (SELECT MAX(sr1.date) FROM StockRanking sr1)
         WHERE uhs.userId = :userId
           AND s.status IN :statuses
         ORDER BY s.status, uhs.displayOrder, st.name
        """)// ORDER BY s.status 는 ACTIVE -> INACTIVE_BY_ADMIN 순서로 정렬하기 위함
    Page<MySolidarityDto> findSortedMySolidarityList(Long userId, List<ag.act.model.Status> statuses, Pageable pageable);

    @Query("""
        SELECT new ag.act.dto.MySolidarityDto(uhs, st, s, mrd, sr) 
        FROM UserHoldingStock uhs
          JOIN FETCH uhs.stock st
          LEFT JOIN FETCH st.solidarity s
          LEFT JOIN FETCH s.solidarityLeader sl
          LEFT JOIN FETCH s.mostRecentDailySummary mrd
          LEFT JOIN StockRanking sr ON sr.stockCode = st.code
           AND sr.date = :date
         WHERE uhs.userId = :userId
           AND s.status IN :statuses
         ORDER BY s.status, uhs.displayOrder, st.name
        """)// ORDER BY s.status 는 ACTIVE -> INACTIVE_BY_ADMIN 순서로 정렬하기 위함
    Page<MySolidarityDto> findSortedMySolidarityListByDate(Long userId, List<ag.act.model.Status> statuses, LocalDate date, Pageable pageable);

    boolean existsByUserId(Long userId);

    boolean existsByUserIdAndStockCodeAndStatus(Long userId, String stockCode, ag.act.model.Status status);

    @Query(value = """
        SELECT uhs.stock_code
         FROM user_holding_stocks uhs
         INNER JOIN solidarities s on uhs.stock_code = s.stock_code
         INNER JOIN solidarity_leaders sl on s.id = sl.solidarity_id and sl.user_id = uhs.user_id
         WHERE uhs.user_id = :userId
         AND s.status = 'ACTIVE'
        """,
        nativeQuery = true)
    List<String> findLeadingSolidarityStockCodes(@Param("userId") Long userId);

    List<UserHoldingStock> findAllByUserIdAndStatus(Long userId, ag.act.model.Status status);

    @Query(value = """
        SELECT uhs.stock_code as title, count(1) as "value"
         FROM user_holding_stocks uhs
         WHERE uhs.status = 'ACTIVE'
         and uhs.created_at <= :endDateTime
         group by uhs.stock_code
        """, nativeQuery = true)
    List<ICountItem> findAllStockUserCount(LocalDateTime endDateTime);

    @Query(value = """
        SELECT uhs.stock_code as title, sum(uhs.quantity) as "value"
         FROM user_holding_stocks uhs
         WHERE uhs.status = 'ACTIVE'
         and uhs.created_at <= :endDateTime
         group by uhs.stock_code
        """, nativeQuery = true)
    List<ICountItem> findAllUserHoldingStockQuantity(LocalDateTime endDateTime);

    @Query(value = """
            SELECT new ag.act.dto.admin.UserDummyStockDto(uhs.stockCode, s.name, uhsord.quantity, srd.referenceDate, uhs.createdAt)
              FROM UserHoldingStock uhs
              INNER JOIN UserHoldingStockOnReferenceDate uhsord
                ON uhsord.stockCode = uhs.stockCode
               AND uhsord.userId = uhs.userId
              INNER JOIN Stock s
                ON s.code = uhsord.stockCode
               AND s.status = 'ACTIVE'
              INNER JOIN User u
                ON u.id = uhs.userId
               AND u.id = :userId
               AND u.status = 'ACTIVE'
              INNER JOIN StockReferenceDate srd
                ON srd.stockCode = uhs.stockCode
               AND srd.referenceDate = uhsord.referenceDate
             WHERE uhs.status IS NULL
        """)
    Page<UserDummyStockDto> findAllByUserIdInAndStatusIsNull(Long userId, Pageable pageable);

    @Query(value = """
        SELECT new ag.act.dto.stock.SimpleStockDto(s.code, s.name, s.standardCode)
          FROM UserHoldingStock uhs
          INNER JOIN Stock s
            ON s.code = uhs.stockCode
           AND uhs.userId = :userId
        """)
    List<SimpleStock> findAllUserHoldingSimpleStocksByUserId(Long userId);

    @Query(value = """
        SELECT uhs.stockCode
        FROM UserHoldingStock uhs
        WHERE uhs.userId = :userId
        """)
    List<String> findAllUserHoldingStockCodesByUserId(Long userId);

    @Query("""
        SELECT uhs.id
            FROM UserHoldingStock uhs
            WHERE uhs.status = 'ACTIVE'
        """)
    List<Long> findAllIds();
}
