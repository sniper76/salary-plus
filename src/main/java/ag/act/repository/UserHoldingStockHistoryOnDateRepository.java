package ag.act.repository;

import ag.act.entity.UserHoldingStockHistoryOnDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserHoldingStockHistoryOnDateRepository extends JpaRepository<UserHoldingStockHistoryOnDate, Long> {
    List<UserHoldingStockHistoryOnDate> findAllByUserIdAndStockCodeOrderByStockCodeAscDateDesc(Long userId, String stockCode);

    @Query(value = """
        SELECT count(distinct(dd.stock_code))
          FROM digital_documents dd
         WHERE dd.target_start_date < :endDateTime
           AND dd.target_end_date >= :startDateTime
           AND dd.type IN :digitalDocumentTypes
           AND EXISTS ( SELECT 1
                          FROM user_holding_stock_history_on_dates uhs
                         WHERE uhs.stock_code = dd.stock_code
                           AND uhs.date BETWEEN dd.target_start_date AND dd.target_end_date
                           AND uhs.date BETWEEN :startDate AND :endDate
                           AND uhs.user_id = :userId
                      )
        """, nativeQuery = true)
    long countStocksByDigitalDocumentDuringAndTypeInAndExistsStocksByUserIdAndDate(
        Long userId,
        List<String> digitalDocumentTypes,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        LocalDate startDate,
        LocalDate endDate
    );

}
