package ag.act.entity.solidarity.election;

import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
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
@Table(name = "solidarity_leader_elections")
public class SolidarityLeaderElection implements ActEntity, LeaderElection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "winner_applicant_id")
    private Long winnerApplicantId;

    @Column(name = "stock_code", nullable = false)
    private String stockCode;

    @Column(name = "candidate_registration_start_date_time", nullable = false)
    private LocalDateTime candidateRegistrationStartDateTime;

    @Column(name = "candidate_registration_end_date_time", nullable = false)
    private LocalDateTime candidateRegistrationEndDateTime;

    @Column(name = "vote_start_date_time", nullable = false)
    private LocalDateTime voteStartDateTime;

    @Column(name = "vote_end_date_time", nullable = false)
    private LocalDateTime voteEndDateTime;

    @Column(name = "vote_closing_date_time")
    private LocalDateTime voteClosingDateTime;

    @Column(name = "display_end_date_time")
    private LocalDateTime displayEndDateTime;

    @Column(name = "total_stock_quantity", nullable = false)
    private long totalStockQuantity = 0;

    @Column(name = "total_vote_stock_quantity", nullable = false)
    private long totalVoteStockQuantity = 0;

    @Column(name = "total_vote_stake", nullable = false)
    private double totalVoteStake = 0.0;

    @Column(name = "candidate_count", nullable = false)
    private int candidateCount = 0;

    @Column(name = "election_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SolidarityLeaderElectionStatus electionStatus;

    @Column(name = "election_status_details", nullable = false)
    @Enumerated(EnumType.STRING)
    private SolidarityLeaderElectionStatusDetails electionStatusDetails;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void setElectionStatusGroup(SolidarityLeaderElectionStatusGroup electionStatusGroup) {
        this.electionStatus = electionStatusGroup.electionStatus();
        this.electionStatusDetails = electionStatusGroup.electionStatusDetails();
    }

    public void finishElectionWithNoCandidate() {
        setElectionStatusGroup(SolidarityLeaderElectionStatusGroup.FINISHED_BY_NO_CANDIDATE_STATUS_GROUP);
        this.setCandidateCount(0);
    }

    public void setVoteClosingDateTime(LocalDateTime voteClosingDateTime) {
        this.voteClosingDateTime = voteClosingDateTime;
        this.displayEndDateTime = LeaderElectionCurrentDateTimeProvider.toKoreanTimeUntilMidnightNextDay(voteClosingDateTime);
    }
}
