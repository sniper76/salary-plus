package ag.act.entity.solidarity.election;

import ag.act.enums.solidarity.election.SolidarityLeaderElectionAnswerType;
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

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "solidarity_leader_election_poll_item_mappings")
public class SolidarityLeaderElectionPollItemMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "solidarity_leader_election_id")
    private Long solidarityLeaderElectionId;

    @Column(name = "solidarity_leader_applicant_id")
    private Long solidarityLeaderApplicantId;

    @Column(name = "poll_item_id")
    private Long pollItemId;

    @Column(name = "election_answer_type")
    @Enumerated(EnumType.STRING)
    private SolidarityLeaderElectionAnswerType electionAnswerType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
