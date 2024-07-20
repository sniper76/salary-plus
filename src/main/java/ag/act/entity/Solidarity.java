package ag.act.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Slf4j
@Entity
@Table(name = "solidarities")
@Getter
@Setter
public class Solidarity implements ActEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "stock_code", nullable = false, unique = true)
    private String stockCode;

    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'INACTIVE_BY_ADMIN'")
    @Enumerated(EnumType.STRING)
    private ag.act.model.Status status;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "most_recent_daily_summary_id")
    private SolidarityDailySummary mostRecentDailySummary;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "second_most_recent_daily_summary_id")
    private SolidarityDailySummary secondMostRecentDailySummary;

    @Column(name = "has_ever_had_leader", columnDefinition = "BOOLEAN DEFAULT false")
    private boolean hasEverHadLeader = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToOne(mappedBy = "solidarity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SolidarityLeader solidarityLeader;

    @OneToOne(mappedBy = "solidarity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Stock stock;
}
