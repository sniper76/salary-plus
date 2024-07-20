package ag.act.entity;

import ag.act.model.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_holding_stock_on_reference_dates")
@Getter
@Setter
@NoArgsConstructor
public class UserHoldingStockOnReferenceDate implements UserHoldingStockBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "stock_code", nullable = false)
    private String stockCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_code", referencedColumnName = "code", insertable = false, updatable = false)
    private Stock stock;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "reference_date", nullable = false)
    private LocalDate referenceDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public UserHoldingStockOnReferenceDate(Long userId, String stockCode, Long quantity, LocalDate referenceDate, Status status) {
        this.userId = userId;
        this.stockCode = stockCode;
        this.quantity = quantity;
        this.referenceDate = referenceDate;
        this.status = status;
    }
}
