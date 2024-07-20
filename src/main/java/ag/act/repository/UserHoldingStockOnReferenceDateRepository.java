package ag.act.repository;

import ag.act.entity.UserHoldingStockOnReferenceDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserHoldingStockOnReferenceDateRepository extends JpaRepository<UserHoldingStockOnReferenceDate, Long> {

    Optional<UserHoldingStockOnReferenceDate> findByUserIdAndStockCodeAndReferenceDate(Long userId, String stockCode, LocalDate referenceDate);

    List<UserHoldingStockOnReferenceDate> findAllByUserId(Long userId);

    @Query(value = """
        select uhsord.id
        from user_holding_stock_on_reference_dates uhsord
             inner join user_holding_stocks uhs
                on uhs.stock_code = uhsord.stock_code
                and uhs.user_id = uhsord.user_id
                and uhs.status = 'ACTIVE'
                and uhs.user_id = :userId
        """, nativeQuery = true)
    List<Long> findAllIdsWithActiveHoldingStocks(Long userId);

    @Query(value = """
        select count(1) as cnt
        from digital_documents d
        inner join user_holding_stock_on_reference_dates uhsord
            on d.stock_reference_date = uhsord.reference_date
            and d.stock_code = uhsord.stock_code
            and uhsord.user_id = :userId
        left outer join post_user_views puv
            on d.post_id = puv.post_id and puv.user_id = :userId
        where d.type = 'DIGITAL_PROXY'
        and d.target_end_date > current_timestamp
        and puv.id is null
        """, nativeQuery = true)
    long countByPostUserViewDigitalDelegateForReferenceDate(Long userId);

    void deleteAllByUserId(Long userId);

    void deleteAllByUserIdAndStockCode(Long userId, String stockCode);

    boolean existsByUserIdAndStockCode(Long userId, String stockCode);
}
