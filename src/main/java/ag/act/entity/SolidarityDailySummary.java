package ag.act.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "solidarity_daily_summaries")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolidarityDailySummary implements ActEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stock_quantity", nullable = false)
    private Long stockQuantity;

    @Column(name = "stake", nullable = false)
    private Double stake;

    @Column(name = "market_value", nullable = false)
    private Long marketValue;

    @Column(name = "member_count", nullable = false)
    private Integer memberCount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static SolidarityDailySummary createWithZeroValues() {
        SolidarityDailySummary summary = new SolidarityDailySummary();
        summary.stockQuantity = 0L;
        summary.stake = 0.0;
        summary.marketValue = 0L;
        summary.memberCount = 0;
        return summary;
    }
}
