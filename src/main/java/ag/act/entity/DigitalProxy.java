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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "digital_proxies")
@Getter
@Setter
public class DigitalProxy implements ActEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "post_id", insertable = false, updatable = false)
    private Long postId;

    @OneToOne(mappedBy = "digitalProxy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Post post;

    @Column(name = "template_id")
    private String templateId;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "template_role")
    private String templateRole;

    @Column(name = "target_start_date", nullable = false)
    private LocalDateTime targetStartDate;

    @Column(name = "target_end_date", nullable = false)
    private LocalDateTime targetEndDate;

    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private ag.act.model.Status status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @BatchSize(size = 100)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "digital_proxy_id")
    private List<DigitalProxyApproval> digitalProxyApprovalList;
}
