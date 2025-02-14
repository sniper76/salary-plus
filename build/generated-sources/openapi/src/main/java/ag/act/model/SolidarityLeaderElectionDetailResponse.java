package ag.act.model;

import java.net.URI;
import java.util.Objects;
import ag.act.model.SolidarityLeaderElectionApplicantDataResponse;
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
 * SolidarityLeaderElectionDetailResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public class SolidarityLeaderElectionDetailResponse {

  private Long solidarityLeaderElectionId;

  private String status;

  private Boolean isVoted;

  private Integer totalVoterCount;

  @Valid
  private List<@Valid SolidarityLeaderElectionApplicantDataResponse> pollApplicants;

  public SolidarityLeaderElectionDetailResponse solidarityLeaderElectionId(Long solidarityLeaderElectionId) {
    this.solidarityLeaderElectionId = solidarityLeaderElectionId;
    return this;
  }

  /**
   * Get solidarityLeaderElectionId
   * @return solidarityLeaderElectionId
  */
  
  @JsonProperty("solidarityLeaderElectionId")
  public Long getSolidarityLeaderElectionId() {
    return solidarityLeaderElectionId;
  }

  public void setSolidarityLeaderElectionId(Long solidarityLeaderElectionId) {
    this.solidarityLeaderElectionId = solidarityLeaderElectionId;
  }

  public SolidarityLeaderElectionDetailResponse status(String status) {
    this.status = status;
    return this;
  }

  /**
   * 투표 상태  - CANDIDATE_REGISTRATION_PENDING_PERIOD(\"후보자 등록 대기\", 0) - CANDIDATE_REGISTRATION_PERIOD(\"후보자 등록 기간\", 1) - VOTE_PERIOD(\"주주대표 투표\", 2) - FINISHED(\"투표마감\", 3) 
   * @return status
  */
  
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public SolidarityLeaderElectionDetailResponse isVoted(Boolean isVoted) {
    this.isVoted = isVoted;
    return this;
  }

  /**
   * 로그인한 유저의 투표 여부
   * @return isVoted
  */
  
  @JsonProperty("isVoted")
  public Boolean getIsVoted() {
    return isVoted;
  }

  public void setIsVoted(Boolean isVoted) {
    this.isVoted = isVoted;
  }

  public SolidarityLeaderElectionDetailResponse totalVoterCount(Integer totalVoterCount) {
    this.totalVoterCount = totalVoterCount;
    return this;
  }

  /**
   * Get totalVoterCount
   * @return totalVoterCount
  */
  
  @JsonProperty("totalVoterCount")
  public Integer getTotalVoterCount() {
    return totalVoterCount;
  }

  public void setTotalVoterCount(Integer totalVoterCount) {
    this.totalVoterCount = totalVoterCount;
  }

  public SolidarityLeaderElectionDetailResponse pollApplicants(List<@Valid SolidarityLeaderElectionApplicantDataResponse> pollApplicants) {
    this.pollApplicants = pollApplicants;
    return this;
  }

  public SolidarityLeaderElectionDetailResponse addPollApplicantsItem(SolidarityLeaderElectionApplicantDataResponse pollApplicantsItem) {
    if (this.pollApplicants == null) {
      this.pollApplicants = new ArrayList<>();
    }
    this.pollApplicants.add(pollApplicantsItem);
    return this;
  }

  /**
   * Get pollApplicants
   * @return pollApplicants
  */
  @Valid 
  @JsonProperty("pollApplicants")
  public List<@Valid SolidarityLeaderElectionApplicantDataResponse> getPollApplicants() {
    return pollApplicants;
  }

  public void setPollApplicants(List<@Valid SolidarityLeaderElectionApplicantDataResponse> pollApplicants) {
    this.pollApplicants = pollApplicants;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SolidarityLeaderElectionDetailResponse solidarityLeaderElectionDetailResponse = (SolidarityLeaderElectionDetailResponse) o;
    return Objects.equals(this.solidarityLeaderElectionId, solidarityLeaderElectionDetailResponse.solidarityLeaderElectionId) &&
        Objects.equals(this.status, solidarityLeaderElectionDetailResponse.status) &&
        Objects.equals(this.isVoted, solidarityLeaderElectionDetailResponse.isVoted) &&
        Objects.equals(this.totalVoterCount, solidarityLeaderElectionDetailResponse.totalVoterCount) &&
        Objects.equals(this.pollApplicants, solidarityLeaderElectionDetailResponse.pollApplicants);
  }

  @Override
  public int hashCode() {
    return Objects.hash(solidarityLeaderElectionId, status, isVoted, totalVoterCount, pollApplicants);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SolidarityLeaderElectionDetailResponse {\n");
    sb.append("    solidarityLeaderElectionId: ").append(toIndentedString(solidarityLeaderElectionId)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    isVoted: ").append(toIndentedString(isVoted)).append("\n");
    sb.append("    totalVoterCount: ").append(toIndentedString(totalVoterCount)).append("\n");
    sb.append("    pollApplicants: ").append(toIndentedString(pollApplicants)).append("\n");
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

