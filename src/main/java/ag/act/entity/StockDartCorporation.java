package ag.act.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stock_dart_corporations")
public class StockDartCorporation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "corp_code", nullable = false)
    private String corpCode;

    @Column(name = "corp_name")
    private String corpName;

    @Column(name = "stock_code")
    private String stockCode;

    @Column(name = "modify_date", nullable = false)
    private String modifyDate;

    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private ag.act.model.Status status;

    @Column(name = "ceo_name")
    private String ceoName;

    @Column(name = "corp_class")
    private String corpClass;

    @Column(name = "jurisdictional_registration_number")
    private String jurisdictionalRegistrationNumber;

    @Column(name = "business_registration_number")
    private String businessRegistrationNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "homepage_url")
    private String homepageUrl;

    @Column(name = "ir_url")
    private String irUrl;

    @Column(name = "representative_phone_number")
    private String representativePhoneNumber;

    @Column(name = "representative_fax_number")
    private String representativeFaxNumber;

    @Column(name = "industry_code")
    private String industryCode;

    @Column(name = "establishment_date")
    private String establishmentDate;

    @Column(name = "account_settlement_month", columnDefinition = "VARCHAR(255) DEFAULT '12'")
    private String accountSettlementMonth;

    @Builder.Default
    @Column(name = "version")
    private long version = 1L; // primitive type with default 1

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void increaseVersion() {
        this.version = this.version + 1L;
    }
}
