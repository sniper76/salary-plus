package ag.act.entity;

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

import java.time.LocalDateTime;

@Entity
@Table(name = "user_holding_stocks")
@Getter
@Setter
@NoArgsConstructor
public class UserHoldingStock implements UserHoldingStockBase {
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

    @Column(name = "cash_quantity")
    private Long cashQuantity;

    @Column(name = "credit_quantity")
    private Long creditQuantity;

    @Column(name = "secure_loan_quantity")
    private Long secureLoanQuantity;

    @Column(name = "purchase_price")
    private Long purchasePrice; // 매입금액

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ag.act.model.Status status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public UserHoldingStock(Long userId, String stockCode, Long quantity) {
        this.userId = userId;
        this.stockCode = stockCode;
        this.quantity = quantity;
    }
}
