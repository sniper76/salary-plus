package ag.act.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "private_stocks")
public class PrivateStock implements ActEntity {

    private static final String PRIVATE_STOCK_MARKET_TYPE = "비상장";

    @Id
    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "total_issued_quantity")
    private Long totalIssuedQuantity;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ag.act.model.Status status = ag.act.model.Status.ACTIVE;

    @Column(name = "standard_code", nullable = false)
    private String standardCode;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "market_type", nullable = false)
    private String marketType = PRIVATE_STOCK_MARKET_TYPE;

    @Column(name = "stock_type", nullable = false)
    private String stockType;

    @Column(name = "closing_price")
    private Integer closingPrice;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
