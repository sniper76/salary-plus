package ag.act.entity.admin;

import ag.act.repository.interfaces.SimpleStock;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_rankings")
@Getter
@Setter
@NoArgsConstructor
public class StockRanking implements SimpleStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "stock_code", nullable = false)
    private String stockCode;

    @Column(name = "stock_name", nullable = false)
    private String stockName;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "stake", nullable = false)
    private Double stake;

    @Column(name = "stake_rank", nullable = false)
    private Integer stakeRank;

    @Column(name = "stake_rank_delta", nullable = false)
    private Integer stakeRankDelta;

    @Column(name = "market_value", nullable = false)
    private Long marketValue;

    @Column(name = "market_value_rank", nullable = false)
    private Integer marketValueRank;

    @Column(name = "market_value_rank_delta", nullable = false)
    private Integer marketValueRankDelta;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public String getCode() {
        return stockCode;
    }

    @Override
    public String getName() {
        return stockName;
    }
}
