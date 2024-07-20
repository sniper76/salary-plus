package ag.act.entity;

import ag.act.enums.BatchStatus;
import ag.act.model.BatchRequest;
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
@Table(name = "batch_logs")
public class BatchLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "batch_name", nullable = false)
    private String batchName;

    @Column(name = "batch_group_name", nullable = false)
    private String batchGroupName;

    @Column(name = "batch_period", nullable = false)
    private Integer batchPeriod;

    @Column(name = "period_time_unit", nullable = false)
    @Enumerated(EnumType.STRING)
    private BatchRequest.PeriodTimeUnitEnum periodTimeUnit;

    @Column(name = "batch_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BatchStatus batchStatus;

    @Column(name = "result", columnDefinition = "text")
    private String result;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}
