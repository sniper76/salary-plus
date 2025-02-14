package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.LeaderElectionWinnerResponse;
import ag.act.model.SolidarityLeaderApplicantResponse;
import ag.act.model.SolidarityLeaderElectionDetailResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * SolidarityLeaderElectionResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class SolidarityLeaderElectionResponse {

  private LeaderElectionWinnerResponse winner;

  @Valid
  private List<@Valid SolidarityLeaderApplicantResponse> applicants;

  private SolidarityLeaderElectionDetailResponse electionDetail;

  public SolidarityLeaderElectionResponse winner(LeaderElectionWinnerResponse winner) {
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

  public SolidarityLeaderElectionResponse applicants(List<@Valid SolidarityLeaderApplicantResponse> applicants) {
    this.applicants = applicants;
    return this;
  }

  public SolidarityLeaderElectionResponse addApplicantsItem(SolidarityLeaderApplicantResponse applicantsItem) {
    if (this.applicants == null) {
      this.applicants = new ArrayList<>();
    }
    this.applicants.add(applicantsItem);
    return this;
  }

  /**
   * Get applicants
   * @return applicants
  */
  @Valid 
  @JsonProperty("applicants")
  public List<@Valid SolidarityLeaderApplicantResponse> getApplicants() {
    return applicants;
  }

  public void setApplicants(List<@Valid SolidarityLeaderApplicantResponse> applicants) {
    this.applicants = applicants;
  }

  public SolidarityLeaderElectionResponse electionDetail(SolidarityLeaderElectionDetailResponse electionDetail) {
    this.electionDetail = electionDetail;
    return this;
  }

  /**
   * Get electionDetail
   * @return electionDetail
  */
  @Valid 
  @JsonProperty("electionDetail")
  public SolidarityLeaderElectionDetailResponse getElectionDetail() {
    return electionDetail;
  }

  public void setElectionDetail(SolidarityLeaderElectionDetailResponse electionDetail) {
    this.electionDetail = electionDetail;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SolidarityLeaderElectionResponse solidarityLeaderElectionResponse = (SolidarityLeaderElectionResponse) o;
    return Objects.equals(this.winner, solidarityLeaderElectionResponse.winner) &&
        Objects.equals(this.applicants, solidarityLeaderElectionResponse.applicants) &&
        Objects.equals(this.electionDetail, solidarityLeaderElectionResponse.electionDetail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(winner, applicants, electionDetail);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SolidarityLeaderElectionResponse {\n");
    sb.append("    winner: ").append(toIndentedString(winner)).append("\n");
    sb.append("    applicants: ").append(toIndentedString(applicants)).append("\n");
    sb.append("    electionDetail: ").append(toIndentedString(electionDetail)).append("\n");
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

