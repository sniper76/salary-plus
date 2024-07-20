package ag.act.repository.admin;

import ag.act.dto.admin.StockRankingDto;
import ag.act.entity.admin.StockRanking;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockRankingRepository extends JpaRepository<StockRanking, Long> {

    @Query(value = """
        select new ag.act.dto.admin.StockRankingDto(
            sds.stockCode,
            s.name,
            sds.date,
            sds.stake,
            rank() over (order by sds.stake desc),
            sds.marketValue,
            rank() over (order by sds.marketValue desc)
        )
        from SolidarityDailyStatistics sds
            inner join Stock s on s.code = sds.stockCode
            left outer join TestStock ts on ts.code = sds.stockCode
        where sds.date = :searchDate
        and ts.code is null
        """)
    List<StockRankingDto> findStockRankingDtoListByDate(@Param("searchDate") LocalDate searchDate);

    List<StockRanking> findAllByDate(LocalDate localDate);

    @Query(value = """
        select s
        from StockRanking s
        where s.date = (select MAX(sr.date) from StockRanking sr)
        """)
    List<StockRanking> findStockRankingTopN(Pageable pageable);

}
