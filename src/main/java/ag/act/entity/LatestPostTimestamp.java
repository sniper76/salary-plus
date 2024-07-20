package ag.act.entity;

import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "latest_post_timestamps")
@Getter
@Setter
public class LatestPostTimestamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_code", updatable = false)
    private Stock stock;

    @Column(name = "board_group", nullable = false)
    @Enumerated(EnumType.STRING)
    private BoardGroup boardGroup;

    @Column(name = "board_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private BoardCategory boardCategory;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
