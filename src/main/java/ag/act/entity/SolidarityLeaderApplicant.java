package ag.act.entity;

import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
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
@Table(name = "solidarity_leader_applicants")
@Getter
@Setter
public class SolidarityLeaderApplicant implements ActEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "solidarity_id", nullable = false)
    private Long solidarityId;

    @Column(name = "solidarity_leader_election_id")
    private Long solidarityLeaderElectionId;

    @Column(name = "version", nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer version;

    @Column(name = "reasons_for_applying", length = 500)
    private String reasonsForApplying;

    @Column(name = "knowledge_of_company_management", length = 500)
    private String knowledgeOfCompanyManagement;

    @Column(name = "goals", length = 500)
    private String goals;

    @Column(name = "comments_for_stock_holder", length = 500)
    private String commentsForStockHolder;

    @Column(name = "save_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SolidarityLeaderElectionApplyStatus applyStatus;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
