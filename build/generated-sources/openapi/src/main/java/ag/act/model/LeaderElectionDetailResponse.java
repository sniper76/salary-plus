package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.LeaderElectionProcessResponse;
import ag.act.model.LeaderElectionWinnerResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * LeaderElectionDetailResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class LeaderElectionDetailResponse {

  private Long solidarityLeaderElectionId;

  private String electionStatus;

  private LeaderElectionWinnerResponse winner;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant startDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.Instant endDate;

  @Valid
  private List<@Valid LeaderElectionProcessResponse> electionProcesses;

  public LeaderElectionDetailResponse solidarityLeaderElectionId(Long solidarityLeaderElectionId) {
    this.solidarityLeaderElectionId = solidarityLeaderElectionId;
    return this;
  }

  /**
   * 선거 아이디
   * @return solidarityLeaderElectionId
  */
  
  @JsonProperty("solidarityLeaderElectionId")
  public Long getSolidarityLeaderElectionId() {
    return solidarityLeaderElectionId;
  }

  public void setSolidarityLeaderElectionId(Long solidarityLeaderElectionId) {
    this.solidarityLeaderElectionId = solidarityLeaderElectionId;
  }

  public LeaderElectionDetailResponse electionStatus(String electionStatus) {
    this.electionStatus = electionStatus;
    return this;
  }

  /**
   * FINISHED or VOTE_PERIOD or CANDIDATE_REGISTRATION_PERIOD
   * @return electionStatus
  */
  
  @JsonProperty("electionStatus")
  public String getElectionStatus() {
    return electionStatus;
  }

  public void setElectionStatus(String electionStatus) {
    this.electionStatus = electionStatus;
  }

  public LeaderElectionDetailResponse winner(LeaderElectionWinnerResponse winner) {
    this.winner = winner;
    return this;
  }

  /**
   * Get winner
   * @return winner
  */
  @Valid 
  @JsonProperty("winner")
  public LeaderElectionWinnerResponse getWinner() {
    return winner;
  }

  public void setWinner(LeaderElectionWinnerResponse winner) {
    this.winner = winner;
  }

  public LeaderElectionDetailResponse startDate(java.time.Instant startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * 선거 시작일
   * @return startDate
  */
  @Valid 
  @JsonProperty("startDate")
  public java.time.Instant getStartDate() {
    return startDate;
  }

  public void setStartDate(java.time.Instant startDate) {
    this.startDate = startDate;
  }

  public LeaderElectionDetailResponse endDate(java.time.Instant endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * 선거 시작일
   * @return endDate
  */
  @Valid 
  @JsonProperty("endDate")
  public java.time.Instant getEndDate() {
    return endDate;
  }

  public void setEndDate(java.time.Instant endDate) {
    this.endDate = endDate;
  }

  public LeaderElectionDetailResponse electionProcesses(List<@Valid LeaderElectionProcessResponse> electionProcesses) {
    this.electionProcesses = electionProcesses;
    return this;
  }

  public LeaderElectionDetailResponse addElectionProcessesItem(LeaderElectionProcessResponse electionProcessesItem) {
    if (this.electionProcesses == null) {
      this.electionProcesses = new ArrayList<>();
    }
    this.electionProcesses.add(electionProcessesItem);
    return this;
  }

  /**
   * Get electionProcesses
   * @return electionProcesses
  */
  @Valid 
  @JsonProperty("electionProcesses")
  public List<@Valid LeaderElectionProcessResponse> getElectionProcesses() {
    return electionProcesses;
  }

  public void setElectionProcesses(List<@Valid LeaderElectionProcessResponse> electionProcesses) {
    this.electionProcesses = electionProcesses;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LeaderElectionDetailResponse leaderElectionDetailResponse = (LeaderElectionDetailResponse) o;
    return Objects.equals(this.solidarityLeaderElectionId, leaderElectionDetailResponse.solidarityLeaderElectionId) &&
        Objects.equals(this.electionStatus, leaderElectionDetailResponse.electionStatus) &&
        Objects.equals(this.winner, leaderElectionDetailResponse.winner) &&
        Objects.equals(this.startDate, leaderElectionDetailResponse.startDate) &&
        Objects.equals(this.endDate, leaderElectionDetailResponse.endDate) &&
        Objects.equals(this.electionProcesses, leaderElectionDetailResponse.electionProcesses);
  }

  @Override
  public int hashCode() {
    return Objects.hash(solidarityLeaderElectionId, electionStatus, winner, startDate, endDate, electionProcesses);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LeaderElectionDetailResponse {\n");
    sb.append("    solidarityLeaderElectionId: ").append(toIndentedString(solidarityLeaderElectionId)).append("\n");
    sb.append("    electionStatus: ").append(toIndentedString(electionStatus)).append("\n");
    sb.append("    winner: ").append(toIndentedString(winner)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    electionProcesses: ").append(toIndentedString(electionProcesses)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

