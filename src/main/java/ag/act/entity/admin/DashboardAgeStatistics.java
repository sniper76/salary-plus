package ag.act.entity.admin;

import ag.act.enums.admin.DashboardStatisticsType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "dashboard_age_statistics")
@Getter
@Setter
public class DashboardAgeStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DashboardStatisticsType type;

    @Column(name = "date", nullable = false)
    private String date;

    @Column(name = "age_10_value", nullable = false)
    private Long age10Value;

    @Column(name = "age_20_value", nullable = false)
    private Long age20Value;

    @Column(name = "age_30_value", nullable = false)
    private Long age30Value;

    @Column(name = "age_40_value", nullable = false)
    private Long age40Value;

    @Column(name = "age_50_value", nullable = false)
    private Long age50Value;

    @Column(name = "age_60_value", nullable = false)
    private Long age60Value;

    @Column(name = "age_70_value", nullable = false)
    private Long age70Value;

    @Column(name = "age_80_value", nullable = false)
    private Long age80Value;

    @Column(name = "age_90_value", nullable = false)
    private Long age90Value;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
