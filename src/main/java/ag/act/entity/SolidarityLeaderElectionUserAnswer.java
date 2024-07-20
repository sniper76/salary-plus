package ag.act.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "solidarity_leader_election_user_answers")
public class SolidarityLeaderElectionUserAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "solidarity_leader_election_id", nullable = false)
    private Long solidarityLeaderElectionId;

    @Column(name = "solidarity_leader_applicant_id", nullable = false)
    private Long solidarityLeaderApplicantId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "stock_quantity", columnDefinition = "BIGINT DEFAULT '0'")
    private Long stockQuantity;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
