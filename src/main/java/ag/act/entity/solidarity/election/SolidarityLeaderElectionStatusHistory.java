package ag.act.entity.solidarity.election;

import ag.act.entity.ActEntity;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatusDetails;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "solidarity_leader_election_status_histories")
public class SolidarityLeaderElectionStatusHistory implements ActEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "solidarity_leader_election_id")
    private Long solidarityLeaderElectionId;

    @Column(name = "push_id")
    private Long pushId;

    @Column(name = "election_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SolidarityLeaderElectionStatus electionStatus;

    @Column(name = "election_status_details", nullable = false)
    @Enumerated(EnumType.STRING)
    private SolidarityLeaderElectionStatusDetails electionStatusDetails;

    @Column(name = "is_slack_notification_sent", nullable = false)
    private boolean isSlackNotificationSent = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
