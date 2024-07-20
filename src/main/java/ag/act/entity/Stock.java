package ag.act.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "stocks")
public class Stock implements ActEntity {
    @Id
    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "total_issued_quantity")
    private Long totalIssuedQuantity;

    @Column(name = "solidarity_id")
    private Long solidarityId;

    @OneToOne
    @JoinColumn(name = "solidarity_id", insertable = false, updatable = false)
    private Solidarity solidarity;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ag.act.model.Status status = ag.act.model.Status.ACTIVE;

    @Column(name = "standard_code", nullable = false)
    private String standardCode;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "market_type", nullable = false)
    private String marketType;

    @Column(name = "stock_type", nullable = false)
    private String stockType;

    @Column(name = "closing_price")
    private Integer closingPrice;

    @Column(name = "representative_phone_number")
    private String representativePhoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "is_private")
    private Boolean isPrivate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}

